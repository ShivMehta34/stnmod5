package net.tacoman.stnmod.handlers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.events.SpiralSlicerDamageEvent;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity.RemovalReason;

@Mod.EventBusSubscriber
public class SpiralSlicerDamageHandler {

    @SubscribeEvent
    public static void onSpiralSlicerDamage(SpiralSlicerDamageEvent event) {
        LivingEntity target = event.getTarget();
        Player player = event.getPlayer();
        float damageAmount = event.getDamageAmount();

        // Apply damage and check if the target should die
        target.hurt(player.damageSources().playerAttack(player), damageAmount);

        // Apply bleed effect
        target.addEffect(new MobEffectInstance(PotionEffectRegistry.BLEED.get(), 100, 1));

        if (target.getHealth() <= 0.0F) {
            target.remove(Entity.RemovalReason.KILLED);
        }
    }
}
