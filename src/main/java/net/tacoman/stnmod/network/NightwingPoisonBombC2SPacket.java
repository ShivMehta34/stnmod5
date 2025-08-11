package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.events.NightwingPoisonBombHandler;
import java.util.function.Supplier;

public class NightwingPoisonBombC2SPacket {
    public NightwingPoisonBombC2SPacket() {}
    public static void encode(NightwingPoisonBombC2SPacket msg, FriendlyByteBuf buf) {}
    public static NightwingPoisonBombC2SPacket decode(FriendlyByteBuf buf) { return new NightwingPoisonBombC2SPacket(); }

    public static void handle(NightwingPoisonBombC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        c.enqueueWork(() -> {
            Player sender = c.getSender();
            if (sender instanceof ServerPlayer) NightwingPoisonBombHandler.activate(sender);
        });
        c.setPacketHandled(true);
    }
}
