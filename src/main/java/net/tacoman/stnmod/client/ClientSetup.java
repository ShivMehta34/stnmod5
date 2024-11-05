package net.tacoman.stnmod.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.tacoman.stnmod.client.renderer.NinjaCloneRenderer;
import net.tacoman.stnmod.client.model.NinjaCloneModel;
import net.tacoman.stnmod.entities.NinjaCloneEntity;
import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.init.EntityRegistry;
import software.bernie.geckolib.GeckoLib;

@Mod.EventBusSubscriber(modid = stnmod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ClientSetup::onClientSetup);
        modEventBus.addListener(ClientSetup::onRegisterRenderers);
        modEventBus.addListener(ClientSetup::onRegisterKeyMappings);
        modEventBus.addListener(ClientSetup::onRegisterLayerDefinitions);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Client-side setup tasks, if any
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.NINJA_CLONE.get(), NinjaCloneRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.SPECIAL_ABILITY_KEY);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NinjaCloneModel.LAYER_LOCATION, NinjaCloneModel::createBodyLayer);
    }
}
