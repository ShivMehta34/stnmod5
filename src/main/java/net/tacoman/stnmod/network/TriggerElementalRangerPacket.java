package net.tacoman.stnmod.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.tacoman.stnmod.handlers.ServerEventHandler;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.function.Supplier;

public class TriggerElementalRangerPacket {

    public TriggerElementalRangerPacket() {}

    public static void encode(TriggerElementalRangerPacket msg, FriendlyByteBuf buf) {}

    public static TriggerElementalRangerPacket decode(FriendlyByteBuf buf) {
        return new TriggerElementalRangerPacket();
    }

    public static void handle(TriggerElementalRangerPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer serverPlayer = ctx.get().getSender();
            if (serverPlayer != null) {
                // Set a flag on the player to indicate their next arrow will summon lightning
                ServerEventHandler.setLightningArrowFlag(serverPlayer);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}


