package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.events.ArmorBreakerHandler;

import java.util.function.Supplier;

public class ArmorBreakerC2SPacket {
    private final int targetId;

    public ArmorBreakerC2SPacket(int targetId) {
        this.targetId = targetId;
    }

    public static void encode(ArmorBreakerC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.targetId);
    }

    public static ArmorBreakerC2SPacket decode(FriendlyByteBuf buf) {
        return new ArmorBreakerC2SPacket(buf.readInt());
    }

    public static void handle(ArmorBreakerC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        c.enqueueWork(() -> {
            Player sender = c.getSender();
            if (!(sender instanceof ServerPlayer sp)) return;
            Entity target = sp.level().getEntity(msg.targetId);
            if (target != null) {
                ArmorBreakerHandler.activateArmorBreaker(sp, target);
            }
        });
        c.setPacketHandled(true);
    }
}
