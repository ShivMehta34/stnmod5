package net.tacoman.stnmod.events;

import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class NightwingPoisonBombHandler {

    // Tuning
    private static final double RADIUS = 10.0D;            // 10-block radius
    private static final int DURATION_TICKS = 20 * 12;     // ~12s
    private static final int SLOW_AMPLIFIER   = 3;         // Slowness IV
    private static final int POISON_AMPLIFIER = 2;         // Poison III
    private static final int WITHER_AMPLIFIER = 1;         // Wither II
    private static final int NAUSEA_AMPLIFIER = 0;         // Nausea I

    // Soft → deep green dust (custom redstone dust)
    private static final DustColorTransitionOptions GREEN_DUST =
            new DustColorTransitionOptions(
                    new Vector3f(0.06f, 0.85f, 0.18f), // bright green
                    new Vector3f(0.02f, 0.40f, 0.10f), // darker green
                    1.0f
            );

    /** Heavy poison/slow/wither/nausea in a 10-block radius (excludes caster). */
    public static void activate(Player player) {
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer sp)) return;
        if (!player.isAlive()) return;

        ServerLevel level = sp.serverLevel();
        Vec3 c = sp.position();

        // Visuals: subtle green ring + light wisps (not dense)
        spawnPoisonVisual(level, c, RADIUS);

        // SFX
        level.playSound(null, sp.blockPosition(), SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.9F, 0.8F);

        // Apply effects to all living entities in radius except caster / creative / spectator
        AABB box = new AABB(c.x - RADIUS, c.y - 2, c.z - RADIUS, c.x + RADIUS, c.y + 2, c.z + RADIUS);
        for (LivingEntity le : level.getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && e != sp)) {
            if (le instanceof Player p && (p.isCreative() || p.isSpectator())) continue;

            le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DURATION_TICKS, SLOW_AMPLIFIER, false, true));
            le.addEffect(new MobEffectInstance(MobEffects.POISON,            DURATION_TICKS, POISON_AMPLIFIER, false, true));
            le.addEffect(new MobEffectInstance(MobEffects.WITHER,            DURATION_TICKS, WITHER_AMPLIFIER, false, true));
            le.addEffect(new MobEffectInstance(MobEffects.CONFUSION,         DURATION_TICKS, NAUSEA_AMPLIFIER, false, true));
        }
    }

    private static void spawnPoisonVisual(ServerLevel level, Vec3 center, double radius) {
        // thin perimeter rings
        int ringPoints = 48;
        for (int i = 0; i < ringPoints; i++) {
            double t = (Math.PI * 2.0) * i / ringPoints;
            double x = center.x + Math.cos(t) * radius;
            double z = center.z + Math.sin(t) * radius;
            double y = center.y + 0.1;

            // green dust + a tiny sneeze fleck for “toxic” hint
            level.sendParticles(GREEN_DUST, x, y, z, 2, 0.05, 0.02, 0.05, 0.0);
            level.sendParticles(ParticleTypes.SNEEZE, x, y + 0.05, z, 1, 0.05, 0.02, 0.05, 0.0);
        }
        // a few wisps inside (keep light)
        for (int i = 0; i < 80; i++) {
            double ox = (level.random.nextDouble() - 0.5) * radius * 1.8;
            double oz = (level.random.nextDouble() - 0.5) * radius * 1.8;
            level.sendParticles(GREEN_DUST, center.x + ox, center.y + 0.3, center.z + oz,
                    1, 0.25, 0.05, 0.25, 0.0);
        }
    }
}
