package net.tacoman.stnmod.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.utils.PlayerDataUtils;
import net.minecraft.world.phys.EntityHitResult;

@Mod.EventBusSubscriber
public class ArrowHitHandler {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (projectile.getOwner() instanceof ServerPlayer player && projectile instanceof AbstractArrow arrow) {
            if (PlayerDataUtils.hasPlayerLegShot(player)) {
                if (event.getRayTraceResult() instanceof EntityHitResult entityHitResult) {
                    if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 4)); // 10 seconds, Slowness V
                        PlayerDataUtils.setPlayerLegShot(player, false);
                    }
                }
            }
        }
    }
}
