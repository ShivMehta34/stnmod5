package net.tacoman.stnmod.commands;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommandRegistrationEvent {
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        ChangeClassCommand.register(event.getDispatcher());
    }
}
