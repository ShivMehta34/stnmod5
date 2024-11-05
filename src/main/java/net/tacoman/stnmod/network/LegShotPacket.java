package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.utils.PlayerDataUtils;

import java.util.function.Supplier;

public class    LegShotPacket {
    public LegShotPacket() {
    }

    public LegShotPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                PlayerDataUtils.setPlayerLegShot(player, true);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
