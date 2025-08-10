// src/main/java/net/tacoman/stnmod/network/ArmShieldBashPacket.java
package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ArmShieldBashPacket {
    private final int durationTicks;
    public ArmShieldBashPacket(int durationTicks) { this.durationTicks = durationTicks; }
    public ArmShieldBashPacket(FriendlyByteBuf buf) { this.durationTicks = buf.readVarInt(); }
    public void toBytes(FriendlyByteBuf buf) { buf.writeVarInt(durationTicks); }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sp = ctx.get().getSender();
            if (sp != null) net.tacoman.stnmod.handlers.ShieldBashHandler.arm(sp, durationTicks);
        });
        ctx.get().setPacketHandled(true);
    }
}
