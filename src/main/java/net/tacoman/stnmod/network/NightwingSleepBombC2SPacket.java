package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.events.NightwingSleepBombHandler;
import java.util.function.Supplier;

public class NightwingSleepBombC2SPacket {
    public NightwingSleepBombC2SPacket() {}
    public static void encode(NightwingSleepBombC2SPacket msg, FriendlyByteBuf buf) {}
    public static NightwingSleepBombC2SPacket decode(FriendlyByteBuf buf) { return new NightwingSleepBombC2SPacket(); }

    public static void handle(NightwingSleepBombC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        c.enqueueWork(() -> {
            Player sender = c.getSender();
            if (sender instanceof ServerPlayer) NightwingSleepBombHandler.activate(sender);
        });
        c.setPacketHandled(true);
    }
}
