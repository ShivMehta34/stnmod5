package net.tacoman.stnmod.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.tacoman.stnmod.handlers.ServerEventHandler;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.function.Supplier;

public class TriggerEarthArrowPacket {

    public TriggerEarthArrowPacket() {}

    public static void encode(TriggerEarthArrowPacket msg, FriendlyByteBuf buf) {}

    public static TriggerEarthArrowPacket decode(FriendlyByteBuf buf) {
        return new TriggerEarthArrowPacket();
    }

    public static void handle(TriggerEarthArrowPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            if (serverPlayer != null) {
                ServerEventHandler.setEarthArrowFlag(serverPlayer);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
