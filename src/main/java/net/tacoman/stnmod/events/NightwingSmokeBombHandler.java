package net.tacoman.stnmod.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NightwingSmokeBombHandler {

    // NBT key for server-side cooldown
    private static final String CD_UNTIL_KEY = "stnmod_nightwing_smoke_cd_until";

    // Tuning knobs
    private static final int COOLDOWN_TICKS = 20 * 60;  // 60s
    private static final double RADIUS = 5.0D;          // effect radius
    private static final int BLIND_TICKS = 20 * 5;      // 5s blindness
    private static final int BLIND_AMP = 0;             // Blindness I
    private static final int PARTICLE_COUNT = 200;      // visual smoke

    /**
     * Activate Smoke Bomb at the Nightwing's current position.
     * Blinds nearby enemies (excludes the caster, creative/spectator players).
     * Enforces a 60s server-side cooldown.
     */
    public static void activate(Player player) {
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer sp)) return;
        if (!player.isAlive()) return;

        ServerLevel level = sp.serverLevel();
        int now = level.getServer().getTickCount();
        var tag = sp.getPersistentData();

        // Cooldown gate
        int cdUntil = tag.getInt(CD_UNTIL_KEY);
        if (now < cdUntil) {
            int secs = Math.max(0, (cdUntil - now) / 20);
            sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("Smoke Bomb on cooldown: " + secs + "s"));
            return;
        }

        // Center & area
        Vec3 pos = sp.position();
        AABB box = new AABB(
                pos.x - RADIUS, pos.y - 1.0D, pos.z - RADIUS,
                pos.x + RADIUS, pos.y + 2.5D, pos.z + RADIUS
        );

        // Apply blindness to others in radius
        for (LivingEntity le : level.getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && e != sp)) {
            if (le instanceof Player p && (p.isCreative() || p.isSpectator())) continue;
            le.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLIND_TICKS, BLIND_AMP, false, true));
        }

        // Visual smoke burst
        level.sendParticles(ParticleTypes.LARGE_SMOKE,
                pos.x, sp.getY() + 1.0D, pos.z,
                PARTICLE_COUNT,
                1.5D, 1.0D, 1.5D, 0.02D
        );

        // Feedback + cooldown
        sp.sendSystemMessage(net.minecraft.network.chat.Component.literal("Smoke Bomb!"));
        tag.putInt(CD_UNTIL_KEY, now + COOLDOWN_TICKS);
    }
}
