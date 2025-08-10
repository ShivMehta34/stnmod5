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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.*;

@Mod.EventBusSubscriber(modid = "stnmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KnightDashingStrikeHandler {

    // ---- Tunables ----
    private static final int   DASH_TICKS        = 10;      // total dash duration
    private static final double SPEED_PER_TICK   = 0.8;     // blocks per tick (0.8 x 10 = ~8 blocks)
    private static final double HIT_MAX_FORWARD  = 5.0;     // how far ahead to hit each tick
    private static final double HIT_HALF_WIDTH   = 1.0;     // half-width of the "line" (side radius)
    private static final double COS_ANGLE_MIN    = 0.7;     // forward cone (0.7 ≈ 45°)
    private static final float  KNOCKBACK        = 0.5f;    // push on hit
    private static final boolean STOP_ON_BLOCK   = true;    // stop early if we slam into a wall

    // Weapon-scaling damage knobs (same style as Whirlwind)
    private static final float DAMAGE_MULTIPLIER = 1.15f;   // hit harder than Whirlwind
    private static final float DAMAGE_FLAT       = 1.0f;    // +1.0 damage
    private static final float MIN_DAMAGE        = 3.0f;    // clamp min (hearts=> 1.0 = half-heart)
    private static final float MAX_DAMAGE        = 20.0f;   // clamp max

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private static class State {
        int endTick;
        int ticks;
        Vec3 dir;                 // normalized horizontal direction at start
        Set<UUID> alreadyHit = new HashSet<>();
    }

    /** C2S entry from StartKnightDashPacket. */
    public static void tryStart(ServerPlayer sp) {
        if (sp == null || !sp.isAlive() || sp.isRemoved()) return;

        // Class check — swap to your PlayerDataUtils flag if that's the source of truth.
        // Using your pattern from Gladiator (Knight usually had Resistance 1 effect):
        if (!sp.hasEffect(PotionEffectRegistry.KNIGHT_STRENGTH.get())) return;

        if (ACTIVE.containsKey(sp.getUUID())) return; // no overlapping dash

        final int now = sp.server.getTickCount();

        // Lock in a forward *horizontal* direction so the dash is clean
        Vec3 look = sp.getLookAngle();
        Vec3 horiz = new Vec3(look.x, 0, look.z);
        if (horiz.lengthSqr() < 1e-4) horiz = new Vec3(sp.getViewVector(1.0f).x, 0, sp.getViewVector(1.0f).z);
        horiz = horiz.normalize();

        State s = new State();
        s.endTick = now + DASH_TICKS;
        s.ticks   = 0;
        s.dir     = horiz;
        ACTIVE.put(sp.getUUID(), s);

        // Start feedback
        sp.level().gameEvent(GameEvent.HIT_GROUND, sp.blockPosition(), GameEvent.Context.of(sp));
        sp.playNotifySound(SoundEvents.TRIDENT_RIPTIDE_2, SoundSource.PLAYERS, 1f, 1.2f);

        // First hit immediately at start (so it always feels responsive)
        doLineHits(sp, s, true);

        // Optional: tell the activating client to do a local dash-camera effect later (if you want)
        net.tacoman.stnmod.network.NetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> sp),
                new net.tacoman.stnmod.network.StartKnightDashPacket() // (optional client visual)
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.side != LogicalSide.SERVER || e.phase != TickEvent.Phase.END) return;
        if (!(e.player instanceof ServerPlayer sp)) return;

        State s = ACTIVE.get(sp.getUUID());
        if (s == null) return;

        final int now = sp.server.getTickCount();
        if (!sp.isAlive() || sp.isRemoved() || now >= s.endTick) {
            ACTIVE.remove(sp.getUUID());
            return;
        }

        // Check collision ahead; stop early if we hit a wall (optional)
        if (STOP_ON_BLOCK) {
            Vec3 start = sp.position().add(0, sp.getEyeHeight(), 0);
            Vec3 end = start.add(s.dir.scale(SPEED_PER_TICK));
            BlockHitResult hit = sp.level().clip(new ClipContext(start, end,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, sp));
            if (hit.getType() == HitResult.Type.BLOCK) {
                ACTIVE.remove(sp.getUUID());
                return;
            }
        }

        // Apply forward velocity (server-authoritative)
        sp.fallDistance = 0f;
        Vec3 dashVel = s.dir.scale(SPEED_PER_TICK);
        sp.setDeltaMovement(dashVel.x, sp.getDeltaMovement().y * 0.2, dashVel.z);
        sp.hurtMarked = true;

        // Show attack spam while dashing (others see it)
        sp.resetAttackStrengthTicker();
        sp.swing(InteractionHand.MAIN_HAND, true);
        if ((s.ticks & 1) == 0) {
            sp.playNotifySound(SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8f, 1.2f);
        }

        // Damage everything in a *line* in front (narrow side radius, forward angle + distance gate)
        doLineHits(sp, s, false);

        // Particles along the path
        if (sp.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.CLOUD,
                    sp.getX(), sp.getY(0.2), sp.getZ(),
                    6, 0.2, 0.1, 0.2, 0.0);
        }

        s.ticks++;
    }

    /** Damage entities in a narrow forward frustum; hit each entity at most once per dash. */
    private static void doLineHits(ServerPlayer sp, State s, boolean initialBurst) {
        if (!(sp.level() instanceof ServerLevel sl)) return;

        // Build a generous AABB around the forward area to collect candidates
        double reach = HIT_MAX_FORWARD + 1.0;
        AABB box = new AABB(
                sp.getX() - HIT_MAX_FORWARD, sp.getY() - 1.0, sp.getZ() - HIT_MAX_FORWARD,
                sp.getX() + HIT_MAX_FORWARD, sp.getY() + 2.0, sp.getZ() + HIT_MAX_FORWARD
        );

        List<LivingEntity> list = sp.level().getEntitiesOfClass(LivingEntity.class, box,
                le -> le != sp && le.isAlive() && !le.isSpectator());

        for (LivingEntity le : list) {
            // Already hit this dash?
            if (s.alreadyHit.contains(le.getUUID())) continue;

            // Vector from player to target
            Vec3 to = le.position().subtract(sp.position());
            Vec3 toHoriz = new Vec3(to.x, 0, to.z);
            double distHoriz = Math.sqrt(toHoriz.lengthSqr());
            if (distHoriz <= 0.0001 || distHoriz > HIT_MAX_FORWARD) continue;

            // Angle gate (in front)
            double cos = dotNorm(s.dir, toHoriz);
            if (cos < COS_ANGLE_MIN) continue;

            // Side width gate: how far to the side from the center line
            // Compute perpendicular distance from target to the forward line
            double side = sideDistance(toHoriz, s.dir); // blocks to the side
            if (Math.abs(side) > HIT_HALF_WIDTH) continue;

            // Hit once per dash
            float damage = computeWeaponScaledDamage(sp, le);
            if (le.hurt(sp.damageSources().playerAttack(sp), damage)) {
                s.alreadyHit.add(le.getUUID());

                // Push forward a bit to sell the line impact
                Vec3 push = s.dir.scale(KNOCKBACK);
                le.push(push.x, 0.05, push.z);
                le.hurtMarked = true;

                // Small sweep particle at the target
                sl.sendParticles(ParticleTypes.SWEEP_ATTACK,
                        le.getX(), le.getY(0.5), le.getZ(),
                        4, 0.2, 0.1, 0.2, 0.01);
            }
        }

        // Extra whoosh on the initial burst
        if (initialBurst) {
            sl.sendParticles(ParticleTypes.POOF,
                    sp.getX() + s.dir.x * 0.5, sp.getY(0.5), sp.getZ() + s.dir.z * 0.5,
                    8, 0.2, 0.2, 0.2, 0.02);
        }
    }

    private static double dotNorm(Vec3 dirNorm, Vec3 vec) {
        // dot(dir, normalize(vec))
        double len = Math.sqrt(vec.lengthSqr());
        if (len < 1e-6) return -1;
        return (dirNorm.x * vec.x + dirNorm.z * vec.z) / len;
    }

    /** Signed perpendicular distance to the forward line in XZ-plane. */
    private static double sideDistance(Vec3 toHoriz, Vec3 dirNorm) {
        // Project target onto perpendicular of dir: perp = (-dz, dx)
        double px = -dirNorm.z, pz = dirNorm.x;
        return (toHoriz.x * px + toHoriz.z * pz); // signed blocks to the side
    }

    /** Damage from weapon + enchants, scaled and clamped. */
    private static float computeWeaponScaledDamage(ServerPlayer sp, LivingEntity target) {
        double base = sp.getAttributeValue(Attributes.ATTACK_DAMAGE);
        ItemStack main = sp.getMainHandItem();
        MobType mobType = target.getMobType();
        float ench = EnchantmentHelper.getDamageBonus(main, mobType);

        float raw = (float)(base + ench);
        float scaled = raw * DAMAGE_MULTIPLIER + DAMAGE_FLAT;
        return Mth.clamp(scaled, MIN_DAMAGE, MAX_DAMAGE);
    }
}
