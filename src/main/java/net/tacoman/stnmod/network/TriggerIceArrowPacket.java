package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.handlers.ServerEventHandler;

import java.util.function.Supplier;

public class TriggerIceArrowPacket {
    // Constructor
    public TriggerIceArrowPacket() {}

    // Decode from buffer (client-to-server)
    public TriggerIceArrowPacket(FriendlyByteBuf buffer) {}

    // Encode to buffer (server-to-client, not needed here)
    public static void encode(TriggerIceArrowPacket packet, FriendlyByteBuf buffer) {
        // Add encoding logic if needed, or leave empty if no data is sent
    }

    public static TriggerIceArrowPacket decode(FriendlyByteBuf buffer) {
        return new TriggerIceArrowPacket(); // Add decoding logic if needed
    }



    // Handle the packet
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Server-side handling
            var player = context.getSender();
            if (player != null) {
                // Activate the Fire Arrow flag
                ServerEventHandler.setIceArrowFlag(player);
            }
        });
        context.setPacketHandled(true);
    }
}