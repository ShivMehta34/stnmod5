package net.tacoman.stnmod.client;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.client.model.NinjaCloneModel;
import net.tacoman.stnmod.client.renderer.NinjaCloneRenderer;
import net.tacoman.stnmod.init.EntityRegistry;
import net.tacoman.stnmod.stnmod;

@Mod.EventBusSubscriber(modid = stnmod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {} // no instances

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Ninja clone (GeckoLib-based renderer)
        event.registerEntityRenderer(EntityRegistry.NINJA_CLONE.get(), NinjaCloneRenderer::new);

        // Shuriken uses vanilla thrown-item renderer
        event.registerEntityRenderer(
                EntityRegistry.SHURIKEN.get(),
                ctx -> new ThrownItemRenderer<>(ctx, 1.0f, true)
        );
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NinjaCloneModel.LAYER_LOCATION, NinjaCloneModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        // Adjust import if your KeyBindings class is in a different package
        event.register(KeyBindings.SPECIAL_ABILITY_KEY);
    }
}
