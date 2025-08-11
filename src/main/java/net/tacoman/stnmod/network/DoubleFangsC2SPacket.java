package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.events.DoubleFangsHandler;

import java.util.function.Supplier;

public class DoubleFangsC2SPacket {
    private final int targetId; // -1 means no explicit target

    public DoubleFangsC2SPacket(int targetId) {
        this.targetId = targetId;
    }

    public static void encode(DoubleFangsC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.targetId);
    }

    public static DoubleFangsC2SPacket decode(FriendlyByteBuf buf) {
        return new DoubleFangsC2SPacket(buf.readInt());
    }

    public static void handle(DoubleFangsC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        c.enqueueWork(() -> {
            Player sender = c.getSender();
            if (!(sender instanceof ServerPlayer sp)) return;
            Entity target = (msg.targetId >= 0) ? sp.level().getEntity(msg.targetId) : null;
            DoubleFangsHandler.activateDoubleFangs(sp, target);
        });
        c.setPacketHandled(true);
    }
}
