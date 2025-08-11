package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.events.NightwingSmokeBombHandler;

import java.util.function.Supplier;

public class NightwingSmokeBombC2SPacket {
    public NightwingSmokeBombC2SPacket() {}

    public static void encode(NightwingSmokeBombC2SPacket msg, FriendlyByteBuf buf) {}
    public static NightwingSmokeBombC2SPacket decode(FriendlyByteBuf buf) { return new NightwingSmokeBombC2SPacket(); }

    public static void handle(NightwingSmokeBombC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        c.enqueueWork(() -> {
            Player sender = c.getSender();
            if (sender instanceof ServerPlayer) {
                NightwingSmokeBombHandler.activate(sender);
            }
        });
        c.setPacketHandled(true);
    }
}
