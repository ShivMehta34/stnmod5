package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.handlers.ServerEventHandler;

import java.util.function.Supplier;

public class TriggerFireArrowPacket {
    // Constructor
    public TriggerFireArrowPacket() {}

    // Decode from buffer (client-to-server)
    public TriggerFireArrowPacket(FriendlyByteBuf buffer) {}

    // Encode to buffer (server-to-client, not needed here)
    public static void encode(TriggerFireArrowPacket packet, FriendlyByteBuf buffer) {
        // Add encoding logic if needed, or leave empty if no data is sent
    }

    public static TriggerFireArrowPacket decode(FriendlyByteBuf buffer) {
        return new TriggerFireArrowPacket(); // Add decoding logic if needed
    }



    // Handle the packet
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Server-side handling
            var player = context.getSender();
            if (player != null) {
                // Activate the Fire Arrow flag
                ServerEventHandler.setFireArrowFlag(player);
            }
        });
        context.setPacketHandled(true);
    }
}