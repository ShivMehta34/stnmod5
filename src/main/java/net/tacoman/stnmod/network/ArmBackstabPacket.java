package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.handlers.BackstabHandler;

import java.util.function.Supplier;

public class ArmBackstabPacket {
    public ArmBackstabPacket() {}
    public static ArmBackstabPacket decode(FriendlyByteBuf buf) { return new ArmBackstabPacket(); }
    public void encode(FriendlyByteBuf buf) {}

    public static void handle(ArmBackstabPacket msg, Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        c.enqueueWork(() -> {
            var sp = c.getSender();
            if (sp != null) BackstabHandler.arm(sp);
        });
        c.setPacketHandled(true);
    }
}
