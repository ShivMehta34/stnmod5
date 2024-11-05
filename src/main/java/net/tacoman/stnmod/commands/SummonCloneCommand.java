package net.tacoman.stnmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.tacoman.stnmod.entities.NinjaCloneEntity;
import net.tacoman.stnmod.init.EntityRegistry;

public class SummonCloneCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("summonclone")
                .then(Commands.argument("type", StringArgumentType.string())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            Level world = player.getCommandSenderWorld();
                            NinjaCloneEntity clone = new NinjaCloneEntity(EntityRegistry.NINJA_CLONE.get(), world);
                            clone.setPos(player.getX(), player.getY(), player.getZ());
                            clone.setOwner(player);
                            world.addFreshEntity(clone);
                            return 1;
                        })));
    }
}
