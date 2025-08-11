package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.events.NightwingGlideHandler;

import java.util.function.Supplier;

public class NightwingGlideToggleC2SPacket {
    private final boolean active;
    public NightwingGlideToggleC2SPacket(boolean active) { this.active = active; }

    public static void encode(NightwingGlideToggleC2SPacket msg, FriendlyByteBuf buf) { buf.writeBoolean(msg.active); }
    public static NightwingGlideToggleC2SPacket decode(FriendlyByteBuf buf) { return new NightwingGlideToggleC2SPacket(buf.readBoolean()); }

    public static void handle(NightwingGlideToggleC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        c.enqueueWork(() -> {
            Player sender = c.getSender();
            if (sender instanceof ServerPlayer sp) {
                NightwingGlideHandler.setGlideActive(sp, msg.active);
            }
        });
        c.setPacketHandled(true);
    }
}
