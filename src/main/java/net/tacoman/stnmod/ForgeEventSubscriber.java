package net.tacoman.stnmod;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = stnmod.MODID)
public class ForgeEventSubscriber {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // Add server starting logic here
    }
}
