package net.tacoman.stnmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.entities.NinjaCloneEntity;
import net.tacoman.stnmod.init.EntityRegistry;

import java.util.function.Supplier;

public class SummonClonesPacket {

    public static void encode(SummonClonesPacket msg, FriendlyByteBuf buf) {}

    public static SummonClonesPacket decode(FriendlyByteBuf buf) {
        return new SummonClonesPacket();
    }

    public static void handle(SummonClonesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ServerLevel world = player.serverLevel();
                if (world != null) {
                    Vec3 position = player.position();

                    NinjaCloneEntity clone1 = new NinjaCloneEntity(EntityRegistry.NINJA_CLONE.get(), world);
                    clone1.setOwner(player);
                    clone1.moveTo(position.x + 1, position.y, position.z + 1);
                    world.addFreshEntity(clone1);

                    NinjaCloneEntity clone2 = new NinjaCloneEntity(EntityRegistry.NINJA_CLONE.get(), world);
                    clone2.setOwner(player);
                    clone2.moveTo(position.x - 1, position.y, position.z - 1);
                    world.addFreshEntity(clone2);

                    player.sendSystemMessage(Component.literal("Successfully summoned clones!"));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
