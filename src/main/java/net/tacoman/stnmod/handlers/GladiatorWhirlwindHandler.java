package net.tacoman.stnmod.handlers;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Gladiator Whirlwind Strike
 * - NO server cooldown (client keybind cooldown only)
 * - Prevents overlapping casts via ACTIVE map
 * - Immediate damage pulse on start + periodic pulses while active
 * - Spam swing animation every tick (sound every other tick)
 * - Damage scales off current weapon (attack damage + enchants) with tunable multiplier/flat add
 */
@Mod.EventBusSubscriber(modid = "stnmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GladiatorWhirlwindHandler {

    // --- Tunables ---
    private static final int   DURATION_TICKS   = 60;       // 3.0s @ 20 TPS
    private static final float RADIUS           = 4.0f;     // AoE radius (XZ)
    private static final int   HIT_EVERY_TICKS  = 4;        // AoE pulse cadence
    private static final float KNOCKBACK        = 0.30f;    // gentle push
    private static final float SPIN_DEG_TICK    = 50f;      // server yaw (cosmetic until we sync)

    // Weapon-scaling damage knobs
    private static final float DAMAGE_MULTIPLIER = 0.85f;   // scale weapon damage by this
    private static final float DAMAGE_FLAT       = 0.5f;    // add this many hearts (0.5 = 1 dmg)
    private static final float MIN_DAMAGE        = 2.0f;    // minimum per target hit (hearts -> 1.0 = half-heart)
    private static final float MAX_DAMAGE        = 16.0f;   // clamp to avoid memes

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private static class State {
        int endTick;
        int ticks; // cadence counter
    }

    /** Called by C2S StartWhirlwindPacket. */
    public static void tryStart(ServerPlayer sp) {
        if (sp == null || !sp.isAlive() || sp.isRemoved()) return;

        // Class check — swap to your PlayerDataUtils flag if that's the source of truth
        if (!sp.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get())) return;

        // Prevent overlapping runs
        if (ACTIVE.containsKey(sp.getUUID())) return;

        final int now = sp.server.getTickCount();

        // Start state
        State s = new State();
        s.endTick = now + DURATION_TICKS;
        s.ticks   = 0;
        ACTIVE.put(sp.getUUID(), s);

        // Optional feedback on start
        sp.level().gameEvent(GameEvent.HIT_GROUND, sp.blockPosition(), GameEvent.Context.of(sp));
        sp.playNotifySound(SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 1f);

        // Guaranteed first hit immediately
        doPulse(sp);

        // ACK to the activating client: start local camera spin + set client-side cooldown
        net.tacoman.stnmod.network.NetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> sp),
                new net.tacoman.stnmod.network.WhirlwindStartedPacket()
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.side != LogicalSide.SERVER || e.phase != TickEvent.Phase.END) return;
        if (!(e.player instanceof ServerPlayer sp)) return;

        State s = ACTIVE.get(sp.getUUID());
        if (s == null) return;

        final int now = sp.server.getTickCount();

        // End when time’s up or player invalid
        if (!sp.isAlive() || sp.isRemoved() || now >= s.endTick) {
            ACTIVE.remove(sp.getUUID());
            return;
        }

        // Spin (others won’t see until we add rotation sync)
        sp.setYRot(sp.getYRot() + SPIN_DEG_TICK);
        sp.yHeadRot = sp.getYRot();
        sp.yBodyRot = sp.getYRot();
        sp.hurtMarked = true;

        // Spam swing every tick (visuals for everyone nearby)
        sp.resetAttackStrengthTicker();                            // keep attack meter "ready"
        sp.swing(InteractionHand.MAIN_HAND, true);                 // broadcast swing animation
        if ((s.ticks & 1) == 0) {                                  // sound every other tick
            sp.playNotifySound(SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 1f);
        }

        // AoE damage on cadence
        if (s.ticks % HIT_EVERY_TICKS == 0) {
            doPulse(sp);
        }
        s.ticks++; // increment AFTER the check to keep cadence stable
    }

    /** Deals AoE damage + tiny knockback; returns count hit (unused, but handy for debugging). */
    private static int doPulse(ServerPlayer sp) {
        if (!(sp.level() instanceof ServerLevel sl)) return 0;

        // Generous Y span so we catch entities on slabs/stairs
        final double yDown = 1.5, yUp = 2.5;
        AABB box = new AABB(
                sp.getX() - RADIUS, sp.getY() - yDown, sp.getZ() - RADIUS,
                sp.getX() + RADIUS, sp.getY() + yUp,  sp.getZ() + RADIUS
        );

        // Permissive filter: exclude only self/spectators/dead
        List<LivingEntity> victims = sp.level().getEntitiesOfClass(
                LivingEntity.class, box,
                le -> le != sp && !le.isSpectator() && le.isAlive()
                // If you have team/friendly-fire logic, add: && !le.isAlliedTo(sp)
        );

        int count = 0;
        for (LivingEntity le : victims) {
            float amount = computeWeaponScaledDamage(sp, le);      // <-- weapon-based damage
            boolean did = le.hurt(sp.damageSources().playerAttack(sp), amount);
            if (did) count++;

            // tiny radial push for feedback
            Vec3 push = le.position().subtract(sp.position()).normalize().scale(KNOCKBACK);
            le.push(push.x, 0.05, push.z);
            le.hurtMarked = true;
        }

        // Light particles
        sl.sendParticles(ParticleTypes.SWEEP_ATTACK,
                sp.getX(), sp.getY(0.5), sp.getZ(),
                6, 0.4, 0.2, 0.4, 0.01);

        return count;
    }

    /** Compute damage from the player's weapon + enchants, scaled by our knobs and clamped. */
    private static float computeWeaponScaledDamage(ServerPlayer sp, LivingEntity target) {
        // Base attack damage includes the weapon's attribute modifiers
        double base = sp.getAttributeValue(Attributes.ATTACK_DAMAGE); // e.g., sword + strength modifiers

        // Enchantment bonus vs target type (e.g., Sharpness, Smite, Bane)
        ItemStack main = sp.getMainHandItem();
        MobType mobType = target.getMobType();
        float ench = EnchantmentHelper.getDamageBonus(main, mobType); // adds extra damage vs that mob type

        // Combine and scale
        float raw = (float) (base + ench);
        float scaled = raw * DAMAGE_MULTIPLIER + DAMAGE_FLAT;

        // Clamp to keep numbers sane
        return Mth.clamp(scaled, MIN_DAMAGE, MAX_DAMAGE);
    }
}
