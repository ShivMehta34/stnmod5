package net.tacoman.stnmod;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.tacoman.stnmod.items.CustomCrossbowItem;
import net.tacoman.stnmod.init.ItemRegistry;

@Mod.EventBusSubscriber(modid = stnmod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventSubscriber {
    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        // Register your client-side resources such as textures, models, etc. here if needed
    }


    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemProperties.register(ItemRegistry.CUSTOM_CROSSBOW.get(), new ResourceLocation("pulling"),
                (stack, level, entity, seed) -> {
                    if (entity != null && entity.isUsingItem() && entity.getUseItem() == stack && !stack.getOrCreateTag().getBoolean("IsCharged")) {
                        return 1.0F;
                    }
                    return 0.0F;
                }
        );

        ItemProperties.register(ItemRegistry.CUSTOM_CROSSBOW.get(), new ResourceLocation("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !entity.isUsingItem() || entity.getUseItem() != stack) {
                        return 0.0F;
                    }

                    int useDuration = stack.getUseDuration();
                    int remaining = entity.getUseItemRemainingTicks();
                    float pullProgress = (useDuration - remaining) / (float) useDuration;

                    return Math.min(pullProgress, 1.0F);
                }
        );

        ItemProperties.register(ItemRegistry.CUSTOM_CROSSBOW.get(), new ResourceLocation("charged"),
                (stack, level, entity, seed) -> stack.getOrCreateTag().getBoolean("IsCharged") ? 1.0F : 0.0F
        );

        ItemProperties.register(ItemRegistry.CUSTOM_CROSSBOW.get(), new ResourceLocation("firework"),
                (stack, level, entity, seed) -> stack.getOrCreateTag().getBoolean("HasFirework") ? 1.0F : 0.0F
        );
    }
}