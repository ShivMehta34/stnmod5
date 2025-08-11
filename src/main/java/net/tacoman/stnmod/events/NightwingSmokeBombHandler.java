package net.tacoman.stnmod.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public class NightwingSmokeBombHandler {

    // --- Tuning knobs ---
    private static final double RADIUS       = 12.0D;    // ≥10 as requested
    private static final int BLIND_TICKS     = 20 * 8;   // 8s Blindness
    private static final int BLIND_AMP       = 1;        // Blindness II
    private static final int DARK_TICKS      = 20 * 6;   // Darkness for players
    private static final int PARTICLE_BURSTS = 6;
    private static final int PARTICLES_PER_BURST = 280;
    private static final double ESCAPE_MIN   = 5.0D;
    private static final double ESCAPE_MAX   = 10.0D;

    /** Big smoke, AoE blind, short teleport. No cooldown here. */
    public static void activate(Player player) {
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer sp)) return;
        if (!player.isAlive()) return;

        ServerLevel level = sp.serverLevel();
        Vec3 center = sp.position();

        // Dense origin cloud + sound
        spawnDenseSmoke(level, center, PARTICLE_BURSTS, PARTICLES_PER_BURST, 2.5D, 1.6D, 2.5D);
        level.playSound(null, sp.blockPosition(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.7F, 0.7F);

        // Blind/Darken everything nearby (except caster/creative/spectator)
        AABB box = new AABB(center.x - RADIUS, center.y - 1.5D, center.z - RADIUS,
                center.x + RADIUS, center.y + 3.0D, center.z + RADIUS);
        for (LivingEntity le : level.getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && e != sp)) {
            if (le instanceof Player p && (p.isCreative() || p.isSpectator())) continue;
            le.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLIND_TICKS, BLIND_AMP, false, true));
            le.addEffect(new MobEffectInstance(MobEffects.DARKNESS, DARK_TICKS, 0, false, true));
        }

        // Escape direction (away from nearest enemy → look dir → random)
        Vec3 escapeDir = computeEscapeVector(level, sp, RADIUS);
        double dist = ThreadLocalRandom.current().nextDouble(ESCAPE_MIN, ESCAPE_MAX);
        Vec3 target = center.add(escapeDir.scale(dist));

        // Snap to safe ground
        BlockPos safe = findSafeGround(level, target.x, center.y, target.z);
        if (safe == null) {
            double ang = ThreadLocalRandom.current().nextDouble(0, Math.PI * 2);
            Vec3 alt = center.add(Math.cos(ang) * 7.0, 0, Math.sin(ang) * 7.0);
            safe = findSafeGround(level, alt.x, center.y, alt.z);
        }
        if (safe != null) {
            sp.teleportTo(safe.getX() + 0.5D, safe.getY(), safe.getZ() + 0.5D);
            Vec3 end = sp.position();
            spawnDenseSmoke(level, end, 3, 240, 1.8D, 1.2D, 1.8D);
            level.playSound(null, sp.blockPosition(), SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.PLAYERS, 0.6F, 1.2F);
        }
    }

    private static void spawnDenseSmoke(ServerLevel level, Vec3 center, int bursts, int perBurst, double sx, double sy, double sz) {
        for (int i = 0; i < bursts; i++) {
            level.sendParticles(ParticleTypes.LARGE_SMOKE, center.x, center.y + 1.0D, center.z, perBurst, sx, sy, sz, 0.02D);
            level.sendParticles(ParticleTypes.CLOUD,       center.x, center.y + 1.0D, center.z, perBurst, sx, sy, sz, 0.0D);
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, center.x, center.y + 0.5D, center.z, perBurst / 3, sx*0.6D, sy, sz*0.6D, 0.01D);
        }
    }

    private static Vec3 computeEscapeVector(ServerLevel level, ServerPlayer sp, double radius) {
        Vec3 pos = sp.position();
        AABB box = new AABB(pos.x - radius, pos.y - 2, pos.z - radius, pos.x + radius, pos.y + 2, pos.z + radius);
        TargetingConditions cond = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting()
                .selector(e -> e.isAlive() && e != sp && (!(e instanceof Player p) || (!p.isCreative() && !p.isSpectator())));
        LivingEntity nearest = level.getNearestEntity(LivingEntity.class, cond, sp, pos.x, pos.y, pos.z, box);
        if (nearest != null) {
            Vec3 away = pos.subtract(nearest.position());
            if (away.lengthSqr() > 1.0E-4) return away.normalize();
        }
        Vec3 look = sp.getLookAngle();
        if (look.lengthSqr() > 1.0E-4) return look.normalize();
        double ang = ThreadLocalRandom.current().nextDouble(0, Math.PI * 2);
        return new Vec3(Math.cos(ang), 0, Math.sin(ang));
    }

    private static BlockPos findSafeGround(ServerLevel level, double x, double yHint, double z) {
        BlockPos baseXZ = BlockPos.containing(x, yHint, z);
        BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, baseXZ);
        BlockPos feet = ground;
        BlockPos head = feet.above();
        if (level.isEmptyBlock(feet) && level.isEmptyBlock(head)) return feet;
        for (int dy = -2; dy <= 3; dy++) {
            BlockPos p = ground.offset(0, dy, 0);
            if (level.isEmptyBlock(p) && level.isEmptyBlock(p.above())) return p;
        }
        return null;
    }
}
