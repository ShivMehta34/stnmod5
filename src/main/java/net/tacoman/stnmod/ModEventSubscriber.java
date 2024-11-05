package net.tacoman.stnmod;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.commands.SummonCloneCommand;

@Mod.EventBusSubscriber(modid = stnmod.MODID)
public class ModEventSubscriber {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SummonCloneCommand.register(event.getDispatcher());
    }
}
