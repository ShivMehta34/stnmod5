package net.tacoman.stnmod.handlers;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Explosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ServerEventHandler {

    // HashMaps to track elemental and earth arrow statuses for each player
    private static final HashMap<UUID, Boolean> elementalRangerFlags = new HashMap<>();
    private static final HashMap<UUID, Boolean> earthArrowFlags = new HashMap<>();

    // Setters for activating each arrow type
    public static void setLightningArrowFlag(ServerPlayer player) {
        elementalRangerFlags.put(player.getUUID(), true);
    }

    public static void setEarthArrowFlag(ServerPlayer player) {
        earthArrowFlags.put(player.getUUID(), true);
    }

    @SubscribeEvent
    public static void onArrowImpact(ProjectileImpactEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow && arrow.getOwner() instanceof ServerPlayer player) {
            UUID playerId = player.getUUID();

            // Determine the exact hit position
            HitResult hitResult = event.getRayTraceResult();
            BlockPos hitPos;
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                hitPos = ((EntityHitResult) hitResult).getEntity().blockPosition();
            } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                hitPos = ((BlockHitResult) hitResult).getBlockPos();
            } else {
                hitPos = arrow.blockPosition();
            }

            ServerLevel world = (ServerLevel) player.getCommandSenderWorld(); // Explicit cast to ServerLevel
            if (world != null) {
                // Check if the player's next arrow should summon lightning or explode
                if (elementalRangerFlags.getOrDefault(playerId, false)) {
                    elementalRangerFlags.put(playerId, false); // Reset the flag

                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
                    lightning.moveTo(Vec3.atBottomCenterOf(hitPos));
                    world.addFreshEntity(lightning);

                    player.sendSystemMessage(Component.literal("Elemental Ranger's lightning arrow struck!"));
                }

                if (earthArrowFlags.getOrDefault(playerId, false)) {
                    earthArrowFlags.put(playerId, false); // Reset the flag

                    // Create and execute the explosion only on the server side
                    Explosion explosion = new Explosion(world, null, hitPos.getX() + 0.5, hitPos.getY(), hitPos.getZ() + 0.5, 8.0F, false, Explosion.BlockInteraction.DESTROY);

                    explosion.explode();
                    explosion.finalizeExplosion(true);

                    player.sendSystemMessage(Component.literal("Earth Arrow exploded on impact!"));
                }
            }
        }
    }

}
