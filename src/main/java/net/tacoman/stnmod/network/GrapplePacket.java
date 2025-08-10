package net.tacoman.stnmod.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.function.Supplier;

public class GrapplePacket {
    public GrapplePacket() {}

    public static void encode(GrapplePacket msg, FriendlyByteBuf buf) {
        // No data to encode
    }

    public static GrapplePacket decode(FriendlyByteBuf buf) {
        return new GrapplePacket();
    }

    public static void handle(GrapplePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Call your grapple method here
                attemptGrapple(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // The method from previous examples
    private static void attemptGrapple(ServerPlayer player) {
        // Check marksman
        if (!isMarksman(player)) {
            player.sendSystemMessage(Component.literal("You lack the marksman ability to grapple."));
            return;
        }

        // Ray trace logic
        Level level = player.getCommandSenderWorld();
        double range = 50.0;
        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle().scale(range);
        Vec3 end = start.add(direction);

        ClipContext context = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        HitResult hit = level.clip(context);

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            Vec3 targetPos = Vec3.atCenterOf(blockHit.getBlockPos());
            startPulling(player, targetPos);
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Vec3 targetPos = entityHit.getEntity().position();
            startPulling(player, targetPos);
        } else {
            player.sendSystemMessage(Component.literal("No valid grapple target found."));
        }
    }

    private static void startPulling(ServerPlayer player, Vec3 targetPos) {
        CompoundTag playerData = player.getPersistentData();
        playerData.putBoolean("IsGrapplePulling", true);
        playerData.putDouble("GrappleTargetX", targetPos.x);
        playerData.putDouble("GrappleTargetY", targetPos.y);
        playerData.putDouble("GrappleTargetZ", targetPos.z);

        player.sendSystemMessage(Component.literal("Grapple launched!"));
    }

    private static boolean isMarksman(ServerPlayer player) {
        // Check if player has MARKSMAN_STRENGTH effect or any other logic
        return player.hasEffect(PotionEffectRegistry.MARKSMAN_STRENGTH.get());
    }
}
