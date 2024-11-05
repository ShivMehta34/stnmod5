package net.tacoman.stnmod.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.init.PotionEffectRegistry;

@Mod.EventBusSubscriber(modid = stnmod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NinjaEffect extends MobEffect {
    public NinjaEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
            // Increase walking speed by 30%
            player.getAbilities().setWalkingSpeed(0.13f); // Default is 0.1

            // Increase sneaking speed by 2 times
            if (player.isCrouching()) {
                player.getAbilities().setWalkingSpeed(0.2f); // 2 times normal sneaking speed
            } else {
                player.getAbilities().setWalkingSpeed(0.13f); // Reset to 30% increased speed
            }

            // Apply Haste for double digging speed
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 1, false, false));
        }
    }
}
