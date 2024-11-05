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
public class ThiefEffect extends MobEffect {
    public ThiefEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get())) {
            // Increase walking speed by 35%
            player.getAbilities().setWalkingSpeed(0.135f); // Default is 0.1

            // Increase sneaking speed by 2.5 times and apply invisibility while sneaking
            if (player.isCrouching()) {
                player.getAbilities().setWalkingSpeed(0.25f); // 2.5 times normal sneaking speed
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, false, false));
            } else {
                player.getAbilities().setWalkingSpeed(0.135f); // Reset to 35% increased speed
                player.removeEffect(MobEffects.INVISIBILITY);
            }

            // Apply Haste for triple digging speed
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 2, false, false));
        }
    }
}
