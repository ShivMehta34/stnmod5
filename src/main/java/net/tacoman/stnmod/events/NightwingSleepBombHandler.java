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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NightwingSleepBombHandler {

    // Tags
    private static final String SLEEP_ACTIVE = "stnmod_sleep_bomb_active";
    private static final String SLEEP_UNTIL  = "stnmod_sleep_bomb_until";

    // Tuning
    private static final double RADIUS           = 10.0D;    // 10-block radius
    private static final int    DURATION_TICKS   = 20 * 6;   // 6s freeze window (tweak if you like)
    private static final int    WEAKNESS_LEVEL   = 4;        // extra safety (we also cancel damage)

    /** Freeze everyone in 10 radius (except caster). Purple, not-dense visuals. */
    public static void activate(Player player) {
        if (player.level().isClientSide) return;
        if (!(player instanceof ServerPlayer sp)) return;
        if (!player.isAlive()) return;

        ServerLevel level = sp.serverLevel();
        int now = level.getServer().getTickCount();
        Vec3 center = sp.position();

        // Apply "sleep" to nearby entities (exclude caster & creative/spectator)
        AABB box = new AABB(center.x - RADIUS, center.y - 2, center.z - RADIUS,
                center.x + RADIUS, center.y + 2, center.z + RADIUS);

        for (LivingEntity le : level.getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && e != sp)) {
            if (le instanceof Player p && (p.isCreative() || p.isSpectator())) continue;

            // Movement lock helpers + tags
            le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DURATION_TICKS, 10, false, true));
            le.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,          DURATION_TICKS, WEAKNESS_LEVEL, false, true));

            le.getPersistentData().putBoolean(SLEEP_ACTIVE, true);
            le.getPersistentData().putInt(SLEEP_UNTIL, now + DURATION_TICKS);
        }

        // Purple ring visuals (not dense)
        spawnPurpleRing(level, center, RADIUS);
        spawnPurpleRing(level, center, RADIUS * 0.6);
        spawnPurpleWisps(level, center);

        // Soft chime
        level.playSound(null, sp.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.7F, 1.3F);
    }

    // ----- Prevent damage while asleep -----
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent e) {
        if (!(e.getSource().getEntity() instanceof LivingEntity attacker)) return;
        var tag = attacker.getPersistentData();
        if (!tag.getBoolean(SLEEP_ACTIVE)) return;

        int until = tag.getInt(SLEEP_UNTIL);
        int now = attacker.level().getServer().getTickCount();
        if (now <= until) {
            e.setCanceled(true); // no damage from sleeping entities
        }
    }

    // ----- Freeze movement & auto-clear tags when time is up -----
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent e) {
        LivingEntity le = e.getEntity();
        if (le.level().isClientSide) return;

        var tag = le.getPersistentData();
        if (!tag.getBoolean(SLEEP_ACTIVE)) return;

        int until = tag.getInt(SLEEP_UNTIL);
        int now = le.level().getServer().getTickCount();

        if (now > until || !le.isAlive()) {
            tag.remove(SLEEP_ACTIVE);
            tag.remove(SLEEP_UNTIL);
            return;
        }

        // Hard-stop horizontal motion (keep downward Y so they can fall)
        Vec3 v = le.getDeltaMovement();
        le.setDeltaMovement(0, Math.min(v.y, 0), 0);
        le.hasImpulse = true;
    }

    // ----- Block ranged attacks while asleep -----

    // Stop NEW projectiles from spawning if the owner is asleep (bows, tridents, shurikens, etc.)
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent e) {
        if (e.getLevel().isClientSide()) return;
        if (!(e.getEntity() instanceof net.minecraft.world.entity.projectile.Projectile proj)) return;

        var owner = proj.getOwner();
        if (owner instanceof LivingEntity le) {
            var tag = le.getPersistentData();
            if (tag.getBoolean(SLEEP_ACTIVE)) {
                e.setCanceled(true); // block spawn
            }
        }
    }

    // Kill any projectile that still exists when its owner is asleep
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent e) {
        if (e.getProjectile().level().isClientSide()) return;
        var owner = e.getProjectile().getOwner();
        if (owner instanceof LivingEntity le && le.getPersistentData().getBoolean(SLEEP_ACTIVE)) {
            e.setCanceled(true);
            e.getProjectile().discard();
        }
    }

    // Cancel bow release while asleep (prevents firing at the source)
    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent e) {
        if (e.getEntity().getPersistentData().getBoolean(SLEEP_ACTIVE)) {
            e.setCanceled(true);
        }
    }

    // Cancel generic right–click item use while asleep (covers many throwables)
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem e) {
        if (e.getLevel().isClientSide()) return;
        if (e.getEntity().getPersistentData().getBoolean(SLEEP_ACTIVE)) {
            e.setCanceled(true);
        }
    }

    // Cancel starting item use while asleep (charging items, etc.)
    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start e) {
        if (e.getEntity().level().isClientSide()) return;
        if (e.getEntity().getPersistentData().getBoolean(SLEEP_ACTIVE)) {
            e.setCanceled(true);
        }
    }

    // ---------- visuals ----------
    private static void spawnPurpleRing(ServerLevel level, Vec3 center, double radius) {
        for (int deg = 0; deg < 360; deg += 8) {
            double rad = Math.toRadians(deg);
            double x = center.x + Math.cos(rad) * radius;
            double z = center.z + Math.sin(rad) * radius;
            BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.containing(x, center.y, z));
            double gy = ground.getY() + 0.05;

            level.sendParticles(ParticleTypes.REVERSE_PORTAL, x, gy, z, 2, 0.05, 0.05, 0.05, 0.0);
            level.sendParticles(ParticleTypes.PORTAL,        x, gy + 0.2, z, 1, 0.05, 0.05, 0.05, 0.0);
        }
    }

    private static void spawnPurpleWisps(ServerLevel level, Vec3 center) {
        for (int i = 0; i < 40; i++) {
            double ox = (level.random.nextDouble() - 0.5) * RADIUS * 2.0;
            double oz = (level.random.nextDouble() - 0.5) * RADIUS * 2.0;
            level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    center.x + ox, center.y + 0.3, center.z + oz,
                    1, 0.2, 0.05, 0.2, 0.0);
        }
    }
}
