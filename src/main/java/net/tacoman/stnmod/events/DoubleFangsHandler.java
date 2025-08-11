package net.tacoman.stnmod.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DoubleFangsHandler {

    // NBT keys
    private static final String DF_ACTIVE      = "stnmod_double_fangs_active";
    private static final String DF_EXPIRES     = "stnmod_double_fangs_expires";
    private static final String DF_HIT_USED    = "stnmod_double_fangs_hit_used";
    private static final String DF_LAUNCHED    = "stnmod_double_fangs_launched";  // to detect landing after launch
    private static final String DF_CD_UNTIL    = "stnmod_double_fangs_cd_until";

    // Tuning knobs
    private static final int   WINDOW_TICKS        = 60;   // ~3.0s to land the mid-air strike
    private static final int   COOLDOWN_TICKS      = 20 * 10; // 10s cooldown (server-side)
    private static final float LAUNCH_UP           = 1.60F;  // was 1.05F
    private static final double LAUNCH_FWD_SPEED   = 1.40;   // was 1.0
    private static final int   JUMP_BOOST_TICKS    = 80;   // was 40 → now ~4s
    private static final int   JUMP_BOOST_LEVEL    = 3;    // was 1 → Jump Boost IV (0-based)

    private static final float DAMAGE_MULTIPLIER   = 4.0F;    // empowered hit multiplier
    private static final double STAGGER_KNOCKBACK  = 0.9;     // shove strength
    private static final int   SLOW_DURATION       = 100;     // 5s
    private static final int   SLOW_AMPLIFIER      = 1;       // Slowness II
    private static final int   WEAK_DURATION       = 100;     // 5s
    private static final int   WEAK_AMPLIFIER      = 0;       // Weakness I

    /**
     * Activate Double Fangs: apply Jump Boost, launch upward/forward, open a one-hit mid-air window.
     * If player misses (lands with no hit), the ability is consumed and enters cooldown.
     */
    public static void activateDoubleFangs(Player player, Entity maybeTarget) {
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer sp)) return;
        if (!player.isAlive()) return;

        ServerLevel level = sp.serverLevel();
        int now = level.getServer().getTickCount();
        var tag = sp.getPersistentData();

        // Cooldown gate (server-side)
        int cdUntil = tag.getInt(DF_CD_UNTIL);
        if (now < cdUntil) {
            int secs = Math.max(0, (cdUntil - now) / 20);
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("Double Fangs on cooldown: " + secs + "s"));
            return;
        }

        // Arm the window (one chance)
        tag.putBoolean(DF_ACTIVE, true);
        tag.putInt(DF_EXPIRES, now + WINDOW_TICKS);
        tag.remove(DF_HIT_USED);
        tag.putBoolean(DF_LAUNCHED, true);

        // Jump Boost for feel & control
        sp.addEffect(new MobEffectInstance(MobEffects.JUMP, JUMP_BOOST_TICKS, JUMP_BOOST_LEVEL, false, true));

        // Launch player upward and forward (toward look dir / optional target)
        Vec3 dir;
        if (maybeTarget instanceof LivingEntity tgt && tgt.isAlive()) {
            Vec3 to = tgt.position().subtract(sp.position());
            dir = new Vec3(to.x, 0, to.z).normalize();
        } else {
            dir = sp.getLookAngle().normalize();
        }
        Vec3 vel = new Vec3(dir.x * LAUNCH_FWD_SPEED, LAUNCH_UP, dir.z * LAUNCH_FWD_SPEED);
        sp.setDeltaMovement(vel);
        sp.hasImpulse = true;

        // Safety timeout: if player never lands a hit, expire window and start cooldown
        TickScheduler.runLater(level, WINDOW_TICKS + 1, () -> {
            if (!sp.isAlive()) return;
            int now2 = level.getServer().getTickCount();
            var tag2 = sp.getPersistentData();
            if (tag2.getBoolean(DF_ACTIVE) && !tag2.getBoolean(DF_HIT_USED)) {
                // missed within time window -> consume and set cooldown
                tag2.remove(DF_ACTIVE);
                tag2.remove(DF_LAUNCHED);
                tag2.remove(DF_HIT_USED);
                tag2.putInt(DF_CD_UNTIL, now2 + COOLDOWN_TICKS);
            }
        });
    }

    /**
     * Empower the first mid-air melee hit within the window. Then consume and set cooldown.
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer sp)) return;
        LivingEntity victim = e.getEntity();

        var tag = sp.getPersistentData();
        if (!tag.getBoolean(DF_ACTIVE) || tag.getBoolean(DF_HIT_USED)) return;

        int now = sp.serverLevel().getServer().getTickCount();
        int expires = tag.getInt(DF_EXPIRES);
        if (now > expires) return;         // window already expired
        if (sp.onGround()) return;         // must be airborne (leaping strike fantasy)

        // Empower damage
        e.setAmount(e.getAmount() * DAMAGE_MULTIPLIER);

        // Stagger (knockback toward look direction)
        Vec3 dir = new Vec3(sp.getLookAngle().x, 0, sp.getLookAngle().z).normalize();
        victim.push(dir.x * STAGGER_KNOCKBACK, 0.2, dir.z * STAGGER_KNOCKBACK);
        victim.hurtMarked = true;

        // Apply debuffs
        victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION, SLOW_AMPLIFIER));
        victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAK_DURATION, WEAK_AMPLIFIER));

        // Consume Double Fangs and start cooldown now
        tag.putBoolean(DF_HIT_USED, true);
        tag.remove(DF_ACTIVE);
        tag.remove(DF_LAUNCHED);
        tag.putInt(DF_CD_UNTIL, now + COOLDOWN_TICKS);

        // Cosmetic swing
        sp.swing(sp.getUsedItemHand(), true);
    }

    /**
     * Detect landing: if the player lands while Double Fangs is armed and no hit was made, consume and set cooldown.
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!(e.player instanceof ServerPlayer sp)) return;

        var tag = sp.getPersistentData();
        if (!tag.getBoolean(DF_ACTIVE)) return;

        // If we launched and have landed without using the hit → fail the attempt
        if (tag.getBoolean(DF_LAUNCHED) && sp.onGround() && !tag.getBoolean(DF_HIT_USED)) {
            int now = sp.serverLevel().getServer().getTickCount();
            tag.remove(DF_ACTIVE);
            tag.remove(DF_LAUNCHED);
            tag.putInt(DF_CD_UNTIL, now + COOLDOWN_TICKS);
        }
    }
}
