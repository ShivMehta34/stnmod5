package net.tacoman.stnmod.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ArmorBreakerHandler {

    private static final String MARKER = "stnmod_armor_breaker_recent";
    private static final UUID ZERO_ARMOR_UUID = UUID.fromString("7bfe1e42-2d7a-4e34-9a6e-7f2a24c7d1a1");
    private static final AttributeModifier ZERO_ARMOR_MOD =
            new AttributeModifier(ZERO_ARMOR_UUID, "stnmod_armor_breaker_zero_armor", -1024.0, AttributeModifier.Operation.ADDITION);

    // Tuning knobs — adjust to taste
    private static final int ROTATIONS    = 6;   // 6 spins
    private static final int HITS_PER_ROT = 6;   // 36 hits total
    private static final int TICK_GAP     = 2;   // ~0.1s per hop → ~3.6s total
    private static final double RADIUS       = 1.25; // stand close enough that hits land
    private static final double JITTER_DEG   = 12.0; // add randomness so you appear all around

    /**
     * SERVER ONLY. Drop-in replacement. Faster orbit, always-facing, unblockable.
     */
    public static void activateArmorBreaker(Player assassin, Entity rawTarget) {
        if (assassin.level().isClientSide) return;
        if (!(assassin instanceof ServerPlayer sp)) return;
        if (!(rawTarget instanceof LivingEntity target)) return;
        if (!assassin.isAlive() || !target.isAlive()) return;

        final ServerLevel level = sp.serverLevel();
        final int attacks = ROTATIONS * HITS_PER_ROT;

        assassin.setInvulnerable(true);

        // Start angle so motion feels immediate
        double baseAngleDeg = ThreadLocalRandom.current().nextDouble(0, 360);

        for (int i = 0; i < attacks; i++) {
            final int idx = i;

            TickScheduler.runLater(level, TICK_GAP * idx, () -> {
                if (!assassin.isAlive() || !target.isAlive()) return;

                // Compute angle: fast spin + slight random jitter each hop so you blink all around
                double progressDeg = (360.0 / HITS_PER_ROT) * (idx % HITS_PER_ROT);
                double jitter = ThreadLocalRandom.current().nextDouble(-JITTER_DEG, JITTER_DEG);
                double angleRad = Math.toRadians(baseAngleDeg + progressDeg + jitter);

                // Orbit point
                Vec3 tPos = target.position();
                double targetX = tPos.x + Math.cos(angleRad) * RADIUS;
                double targetZ = tPos.z + Math.sin(angleRad) * RADIUS;

                // Ground snap; skip if the ground step is absurd (> 1 block difference) to keep flow smooth
                BlockPos base = BlockPos.containing(targetX, tPos.y, targetZ);
                int groundY = assassin.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, base).getY();
                if (Math.abs(groundY - tPos.y) > 1.0) {
                    // too big a step → try without moving Y (prevents getting stuck on cliffs)
                    groundY = (int)Math.floor(assassin.getY());
                }

                // Teleport and face the target
                sp.teleportTo(targetX, groundY, targetZ);

                double lookDx = tPos.x - assassin.getX();
                double lookDz = tPos.z - assassin.getZ();
                double lookDy = (tPos.y + target.getBbHeight() * 0.5) - (assassin.getY() + assassin.getEyeHeight());
                float yaw   = (float)(Math.atan2(lookDz, lookDx) * 180.0 / Math.PI) - 90f;
                float pitch = (float)(-Math.atan2(lookDy, Math.sqrt(lookDx * lookDx + lookDz * lookDz)) * 180.0 / Math.PI);
                assassin.setYRot(yaw);
                assassin.setXRot(pitch);
                assassin.yHeadRot = yaw;
                assassin.yBodyRot = yaw;

                // Prevent vanilla i-frames from nulling subsequent hits
                target.invulnerableTime = 0;

                // Make it "unblockable": briefly disable shields and zero armor for this tick
                if (target instanceof Player tp && tp.isBlocking()) {
                    tp.disableShield(true); // forces shield drop/cooldown
                }

                AttributeInstance armorAttr = target.getAttribute(Attributes.ARMOR);
                boolean applied = false;
                if (armorAttr != null && armorAttr.getModifier(ZERO_ARMOR_UUID) == null) {
                    armorAttr.addTransientModifier(ZERO_ARMOR_MOD);
                    applied = true;
                }
                final AttributeInstance finalArmorAttr = armorAttr;
                final boolean finalApplied = applied;

                // Tag so your reflect/other handlers won't cancel this damage
                target.getPersistentData().putBoolean(MARKER, true);

                // Damage calc (balanced). Reset attack strength so “spam” hits connect.
                assassin.resetAttackStrengthTicker();

// Get base weapon attack damage (Forge 1.20.1 style)
                float weaponBase = 1.0F; // fallback if no weapon
                if (!assassin.getMainHandItem().isEmpty()) {
                    var modifiers = assassin.getMainHandItem()
                            .getAttributeModifiers(net.minecraft.world.entity.EquipmentSlot.MAINHAND)
                            .get(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                    if (!modifiers.isEmpty()) {
                        weaponBase = (float) modifiers.iterator().next().getAmount();
                    }
                }

                float damage = weaponBase * 0.75F; // 75% of weapon base per hit

                // Bleed rider
                target.addEffect(new MobEffectInstance(PotionEffectRegistry.BLEED.get(), 100, 1));

                // Cleanup: remove marker & temp armor right away
                TickScheduler.runLater(level, 0, () -> {
                    target.getPersistentData().remove(MARKER);
                    if (finalApplied && finalArmorAttr != null && finalArmorAttr.getModifier(ZERO_ARMOR_UUID) != null) {
                        finalArmorAttr.removeModifier(ZERO_ARMOR_UUID);
                    }
                });
            });
        }

        // End invulnerability
        TickScheduler.runLater(level, TICK_GAP * attacks, () -> assassin.setInvulnerable(false));
    }
}
