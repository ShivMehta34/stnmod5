package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.utils.PlayerDataUtils;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import java.util.Random;
import java.util.function.Supplier;

public class ArrowRainPacket {
    public ArrowRainPacket() {}

    public static void encode(ArrowRainPacket packet, FriendlyByteBuf buffer) {}

    public static ArrowRainPacket decode(FriendlyByteBuf buffer) {
        return new ArrowRainPacket();
    }

    public static void handle(ArrowRainPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                if (player.hasEffect(PotionEffectRegistry.RANGER_STRENGTH.get())) {
                    ServerLevel world = player.serverLevel(); // Correct method to get the server level
                    Vec3 position = player.position();
                    Random random = new Random();

                    // Create a rain of arrows above the player's target location
                    for (int i = 0; i < 60; i++) {
                        Arrow arrow = new Arrow(world, position.x + random.nextGaussian() * 2, position.y + 15 + random.nextGaussian() * 2, position.z + random.nextGaussian() * 2);
                        arrow.shoot(player.getLookAngle().x, -1.0, player.getLookAngle().z, 1.5F, 0.1F);
                        world.addFreshEntity(arrow);
                    }
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
