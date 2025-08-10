package net.tacoman.stnmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Supplier;

public class WhirlwindStartedPacket {
    public WhirlwindStartedPacket() {}
    public WhirlwindStartedPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    // start local spin only when server approved
                    net.tacoman.stnmod.client.WhirlwindClientSpin.startForMs(3000);
                    var p = Minecraft.getInstance().player;
                    if (p != null) {
                        p.sendSystemMessage(net.minecraft.network.chat.Component.literal("Whirlwind Strike!"));
                    }
                })
        );
        ctx.get().setPacketHandled(true);
    }
}
