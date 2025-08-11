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

    private static final String DF_ACTIVE   = "stnmod_double_fangs_active";
    private static final String DF_EXPIRES  = "stnmod_double_fangs_expires";
    private static final String DF_HIT_USED = "stnmod_double_fangs_hit_used";
    private static final String DF_LAUNCHED = "stnmod_double_fangs_launched";

    private static final int   WINDOW_TICKS       = 60;    // ~3s
    private static final float LAUNCH_UP          = 1.60F; // your juiced values are fine
    private static final double LAUNCH_FWD_SPEED  = 1.40;
    private static final int   JUMP_BOOST_TICKS   = 80;
    private static final int   JUMP_BOOST_LEVEL   = 3;

    private static final float DAMAGE_MULTIPLIER  = 4.0F;
    private static final double STAGGER_KNOCKBACK = 0.9;
    private static final int   SLOW_DURATION      = 100;
    private static final int   SLOW_AMPLIFIER     = 1;
    private static final int   WEAK_DURATION      = 100;
    private static final int   WEAK_AMPLIFIER     = 0;

    public static void activateDoubleFangs(Player player, Entity maybeTarget) {
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer sp)) return;
        if (!player.isAlive()) return;

        ServerLevel level = sp.serverLevel();
        int now = level.getServer().getTickCount();
        var tag = sp.getPersistentData();

        // Arm the one-chance window (no cooldown gating here)
        tag.putBoolean(DF_ACTIVE, true);
        tag.putInt(DF_EXPIRES, now + WINDOW_TICKS);
        tag.remove(DF_HIT_USED);
        tag.putBoolean(DF_LAUNCHED, true);

        // Jump boost + launch
        sp.addEffect(new MobEffectInstance(MobEffects.JUMP, JUMP_BOOST_TICKS, JUMP_BOOST_LEVEL, false, true));
        Vec3 dir;
        if (maybeTarget instanceof LivingEntity tgt && tgt.isAlive()) {
            Vec3 to = tgt.position().subtract(sp.position());
            dir = new Vec3(to.x, 0, to.z).normalize();
        } else {
            dir = sp.getLookAngle().normalize();
        }
        sp.setDeltaMovement(new Vec3(dir.x * LAUNCH_FWD_SPEED, LAUNCH_UP, dir.z * LAUNCH_FWD_SPEED));
        sp.hasImpulse = true;

        // Timeout: if no hit in time, just clear (no cooldown here)
        TickScheduler.runLater(level, WINDOW_TICKS + 1, () -> {
            if (!sp.isAlive()) return;
            var t = sp.getPersistentData();
            if (t.getBoolean(DF_ACTIVE) && !t.getBoolean(DF_HIT_USED)) {
                t.remove(DF_ACTIVE);
                t.remove(DF_LAUNCHED);
            }
        });
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent e) {
        if (!(e.getSource().getEntity() instanceof ServerPlayer sp)) return;
        LivingEntity victim = e.getEntity();

        var tag = sp.getPersistentData();
        if (!tag.getBoolean(DF_ACTIVE) || tag.getBoolean(DF_HIT_USED)) return;

        int now = sp.serverLevel().getServer().getTickCount();
        int expires = tag.getInt(DF_EXPIRES);
        if (now > expires) return;
        if (sp.onGround()) return;

        e.setAmount(e.getAmount() * DAMAGE_MULTIPLIER);
        Vec3 dir = new Vec3(sp.getLookAngle().x, 0, sp.getLookAngle().z).normalize();
        victim.push(dir.x * STAGGER_KNOCKBACK, 0.2, dir.z * STAGGER_KNOCKBACK);
        victim.hurtMarked = true;
        victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION, SLOW_AMPLIFIER));
        victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAK_DURATION, WEAK_AMPLIFIER));

        tag.putBoolean(DF_HIT_USED, true);
        tag.remove(DF_ACTIVE);
        tag.remove(DF_LAUNCHED);

        sp.swing(sp.getUsedItemHand(), true);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!(e.player instanceof ServerPlayer sp)) return;

        var tag = sp.getPersistentData();
        if (!tag.getBoolean(DF_ACTIVE)) return;

        if (tag.getBoolean(DF_LAUNCHED) && sp.onGround() && !tag.getBoolean(DF_HIT_USED)) {
            tag.remove(DF_ACTIVE);
            tag.remove(DF_LAUNCHED);
        }
    }
}