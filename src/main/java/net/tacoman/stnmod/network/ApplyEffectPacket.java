package net.tacoman.stnmod.network;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ApplyEffectPacket {
    private final String command;

    public ApplyEffectPacket(String command) {
        this.command = command;
    }

    public ApplyEffectPacket(FriendlyByteBuf buf) {
        this.command = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(command);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                executeCommandAsOp(player, command);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private void executeCommandAsOp(ServerPlayer player, String command) {
        boolean wasOp = player.getServer().getPlayerList().isOp(player.getGameProfile());
        if (!wasOp) {
            player.getServer().getPlayerList().op(player.getGameProfile());
        }
        try {
            CommandDispatcher<CommandSourceStack> dispatcher = player.getServer().getCommands().getDispatcher();
            CommandSourceStack commandSourceStack = player.createCommandSourceStack().withSuppressedOutput().withPermission(4);
            dispatcher.execute(command, commandSourceStack);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        } finally {
            if (!wasOp) {
                player.getServer().getPlayerList().deop(player.getGameProfile());
            }
        }
    }
}
