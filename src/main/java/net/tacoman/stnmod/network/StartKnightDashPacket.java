package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartKnightDashPacket {
    public StartKnightDashPacket() {}
    public StartKnightDashPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sp = ctx.get().getSender();
            if (sp != null) net.tacoman.stnmod.handlers.KnightDashingStrikeHandler.tryStart(sp);
        });
        ctx.get().setPacketHandled(true);
    }
}
