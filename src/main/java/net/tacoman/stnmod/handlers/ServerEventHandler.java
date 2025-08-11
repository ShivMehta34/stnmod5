package net.tacoman.stnmod.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.tacoman.stnmod.items.CustomCrossbowItem;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber
public class ServerEventHandler {
    private static final HashMap<UUID, Boolean> lightningArrowFlags = new HashMap<>();
    private static final HashMap<UUID, Boolean> earthArrowFlags = new HashMap<>();
    private static final HashMap<UUID, Boolean> fireArrowFlags = new HashMap<>();
    private static final HashMap<UUID, Boolean> iceArrowFlags = new HashMap<>();
    private static final Map<UUID, String> marksmanModes = new HashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    private static final UUID MARKSMAN_SPEED_MODIFIER_UUID = UUID.fromString("e3f6aef1-992c-467f-a8df-7a1a1e34e3f1");


    public static void setLightningArrowFlag(ServerPlayer player) {
        lightningArrowFlags.put(player.getUUID(), true);
    }

    public static void setEarthArrowFlag(ServerPlayer player) {
        earthArrowFlags.put(player.getUUID(), true);
    }

    public static void setFireArrowFlag(ServerPlayer player) {
        fireArrowFlags.put(player.getUUID(), true);
    }

    public static void setIceArrowFlag(ServerPlayer player) {
        iceArrowFlags.put(player.getUUID(), true);
    }


    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AttributeInstance drawSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
            if (drawSpeed != null && drawSpeed.getModifier(MARKSMAN_SPEED_MODIFIER_UUID) != null) {
                drawSpeed.removeModifier(MARKSMAN_SPEED_MODIFIER_UUID);
            }
        }
    }

    @SubscribeEvent
    public static void onArrowImpact(ProjectileImpactEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow && arrow.getOwner() instanceof ServerPlayer player) {
            UUID playerId = player.getUUID();

            if (player.getCommandSenderWorld() instanceof ServerLevel world) {
                // Determine the exact hit position
                HitResult hitResult = event.getRayTraceResult();
                BlockPos hitPos = switch (hitResult.getType()) {
                    case ENTITY -> ((EntityHitResult) hitResult).getEntity().blockPosition();
                    case BLOCK -> ((BlockHitResult) hitResult).getBlockPos();
                    default -> arrow.blockPosition();
                };

                // Lightning Arrow
                if (lightningArrowFlags.getOrDefault(playerId, false)) {
                    lightningArrowFlags.put(playerId, false); // Reset the flag

                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
                    lightning.moveTo(Vec3.atBottomCenterOf(hitPos));
                    world.addFreshEntity(lightning);

                    player.sendSystemMessage(Component.literal("Lightning Arrow struck!"));
                }

                // Earth Arrow
                if (earthArrowFlags.getOrDefault(playerId, false)) {
                    earthArrowFlags.put(playerId, false); // Reset the flag

                    Map<BlockPos, BlockState> originalBlockStates = new HashMap<>();
                    int radius = 4; // Define a radius for the circular area
                    for (BlockPos pos : BlockPos.betweenClosed(hitPos.offset(-radius, -1, -radius), hitPos.offset(radius, 1, radius))) {
                        double distance = pos.distSqr(hitPos);
                        if (distance <= radius * radius) {
                            BlockState currentState = world.getBlockState(pos);
                            if (!currentState.isAir()) {
                                originalBlockStates.put(pos.immutable(), currentState);
                                world.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3); // Replace with dirt temporarily
                            }
                        }
                    }
// Create and execute the explosion only on the server side
                    Explosion explosion = new Explosion(world, null, hitPos.getX() + 0.5, hitPos.getY(), hitPos.getZ() + 0.5, 8.0F, false, Explosion.BlockInteraction.DESTROY);
                    explosion.explode();
                    explosion.finalizeExplosion(true);


                    scheduler.schedule(() -> {
                        for (Map.Entry<BlockPos, BlockState> entry : originalBlockStates.entrySet()) {
                            world.setBlock(entry.getKey(), entry.getValue(), 3);
                        }
                    }, 10, TimeUnit.SECONDS);

                    player.sendSystemMessage(Component.literal("Earth Arrow exploded and reshaped the ground!"));
                }

                // Fire Arrow
                if (fireArrowFlags.getOrDefault(playerId, false)) {
                    fireArrowFlags.put(playerId, false); // Reset the flag

                    Map<BlockPos, BlockState> originalBlockStates = new HashMap<>();
                    int radius = 4;

                    // Iterate through a cube and filter for circular surface blocks
                    for (BlockPos pos : BlockPos.betweenClosed(hitPos.offset(-radius, -1, -radius), hitPos.offset(radius, 1, radius))) {
                        double distance = pos.distSqr(hitPos);
                        if (distance <= radius * radius) {
                            BlockState currentState = world.getBlockState(pos);
                            if (!currentState.isAir() && world.getBlockState(pos.above()).isAir()) { // Replace ground-level blocks only
                                originalBlockStates.put(pos.immutable(), currentState);
                                world.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
                            }
                        }
                    }

                    // Apply fire damage to entities in the area for 30 seconds
                    for (int i = 0; i < 30; i++) { // 30 seconds duration
                        int delay = i;
                        scheduler.schedule(() -> {
                            world.getEntitiesOfClass(LivingEntity.class, new AABB(hitPos).inflate(radius)).forEach(entity -> {
                                entity.setSecondsOnFire(15); // Burn entities for 5 seconds
                                entity.hurt(entity.damageSources().onFire(), 2.0F); // Apply fire damage
                            });
                        }, delay, TimeUnit.SECONDS);
                    }

                    // Schedule reversion after 10 seconds
                    scheduler.schedule(() -> {
                        for (Map.Entry<BlockPos, BlockState> entry : originalBlockStates.entrySet()) {
                            BlockPos pos = entry.getKey();
                            BlockState originalState = entry.getValue();
                            if (world.getBlockState(pos).is(Blocks.MAGMA_BLOCK)) { // Ensure it's still a magma block
                                world.setBlock(pos, originalState, 3);
                            }
                        }
                    }, 30, TimeUnit.SECONDS);

                    player.sendSystemMessage(Component.literal("Fire Arrow created a circular magma field!"));
                }

// Ice Arrow
                if (iceArrowFlags.getOrDefault(playerId, false)) {
                    iceArrowFlags.put(playerId, false); // Reset the flag

                    Map<BlockPos, BlockState> originalBlockStates = new HashMap<>();
                    int radius = 4;

                    // Iterate through a cube and filter for circular surface blocks
                    for (BlockPos pos : BlockPos.betweenClosed(hitPos.offset(-radius, -1, -radius), hitPos.offset(radius, 1, radius))) {
                        double distance = pos.distSqr(hitPos);
                        if (distance <= radius * radius) {
                            BlockState currentState = world.getBlockState(pos);
                            if (!currentState.isAir() && world.getBlockState(pos.above()).isAir()) { // Replace ground-level blocks only
                                originalBlockStates.put(pos.immutable(), currentState);
                                world.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                            }
                        }
                    }

                    // Apply fire damage to entities in the area for 30 seconds
                    for (int i = 0; i < 30; i++) { // 30 seconds duration
                        int delay = i;
                        scheduler.schedule(() -> {
                            world.getEntitiesOfClass(LivingEntity.class, new AABB(hitPos).inflate(radius)).forEach(entity -> {
                                entity.hurt(entity.damageSources().freeze(), 2.0F); // Apply freeze damage
                                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1)); // Apply slowness for 3 seconds
                            });
                        }, delay, TimeUnit.SECONDS);
                    }

                    // Schedule reversion after 10 seconds
                    scheduler.schedule(() -> {
                        for (Map.Entry<BlockPos, BlockState> entry : originalBlockStates.entrySet()) {
                            BlockPos pos = entry.getKey();
                            BlockState originalState = entry.getValue();
                            if (world.getBlockState(pos).is(Blocks.ICE)) { // Ensure it's still an ice block
                                world.setBlock(pos, originalState, 3);
                            }
                        }
                    }, 30, TimeUnit.SECONDS);

                    player.sendSystemMessage(Component.literal("Ice Arrow created a circular ice field!"));
                }

            }


        }
    }



    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof AbstractArrow arrow && arrow.getOwner() instanceof ServerPlayer player) {
            ItemStack weapon = player.getMainHandItem();
            if (weapon.getItem() instanceof CustomCrossbowItem) { // Ensure it's the custom crossbow
                String mode = ServerEventHandler.getMarksmanMode(player);
                Vec3 originalVelocity = arrow.getDeltaMovement();

                switch (mode) {
                    case "Quickfire" -> {
                        arrow.setBaseDamage(arrow.getBaseDamage() * 0.6); // Reduced damage
                        Vec3 newVelocity = originalVelocity.scale(1.0); // Faster arrows
                        arrow.setDeltaMovement(newVelocity);
                        //  player.sendSystemMessage(Component.literal("Quickfire Mode: Reduced damage but faster arrows!"));
                    }
                    case "Concentrate" -> {
                        arrow.setBaseDamage(arrow.getBaseDamage() * 2.0); // High damage
                        Vec3 newVelocity = originalVelocity.scale(1.0); // Slower arrows
                        arrow.setDeltaMovement(newVelocity);
                        //   player.sendSystemMessage(Component.literal("Concentrate Mode: High damage with slower arrows!"));
                    }
                    case "Penetrate" -> {
                        arrow.setBaseDamage(arrow.getBaseDamage() * 1.3); // Moderate damage
                        arrow.setPierceLevel((byte) 12); // Allows piercing multiple entities
                        Vec3 newVelocity = originalVelocity.scale(1.0); // Slightly faster arrows
                        arrow.setDeltaMovement(newVelocity);
                        //  player.sendSystemMessage(Component.literal("Penetrate Mode: Arrows pierce through multiple targets with moderate speed!"));
                    }
                }
            }
        }
    }



    public static String getMarksmanMode(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        return data.getString("MarksmanMode").isEmpty() ? "Quickfire" : data.getString("MarksmanMode");
    }

    public static void setMarksmanMode(ServerPlayer player, String mode) {
        CompoundTag data = player.getPersistentData();
        data.putString("MarksmanMode", mode);
    }


    // Toggle Marksman Mode
    public static void toggleMarksmanMode(ServerPlayer player) {
        String current = getMarksmanMode(player);
        String next = switch (current) {
            case "Quickfire" -> "Concentrate";
            case "Concentrate" -> "Penetrate";
            case "Penetrate" -> "Quickfire";
            default -> "Quickfire";
        };

        setMarksmanMode(player, next);
        player.sendSystemMessage(Component.literal("Marksman Mode: " + next));

        // Apply Quick Charge III if Quickfire mode is toggled
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof CrossbowItem) {
            Map<Enchantment, Integer> enchants = new HashMap<>(EnchantmentHelper.getEnchantments(mainHand));
            if ("Quickfire".equals(next)) {
                enchants.put(Enchantments.QUICK_CHARGE, 3);
            } else {
                enchants.remove(Enchantments.QUICK_CHARGE);
            }
            EnchantmentHelper.setEnchantments(enchants, mainHand);

            // Notify the player to release and re-press right-click for changes to take effect
            player.sendSystemMessage(Component.literal("Quickfire mode activated! Release and re-press right-click to enable faster charging."));
        }
    }



    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // This code runs once per server tick, after all other server tick logic
            // Perform your grapple pull logic here

            // Get the server from the event or from a known source:
            // event doesn't directly give a level, so you might need to store a reference
            // to the server or retrieve it from a static context.
            // For simplicity, if you have access to the MinecraftServer:
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                // Get a reference to a server-level (e.g., the overworld):
                ServerLevel level = server.overworld(); // or another dimension
                for (ServerPlayer player : level.players()) {
                    CompoundTag data = player.getPersistentData();
                    if (data.getBoolean("IsGrapplePulling")) {
                        double tx = data.getDouble("GrappleTargetX");
                        double ty = data.getDouble("GrappleTargetY");
                        double tz = data.getDouble("GrappleTargetZ");
                        Vec3 targetPos = new Vec3(tx, ty, tz);

                        Vec3 playerPos = player.position();
                        Vec3 direction = targetPos.subtract(playerPos);
                        double distance = direction.length();

                        if (distance < 0.5) {
                            data.putBoolean("IsGrapplePulling", false);
                            player.sendSystemMessage(Component.literal("You have reached the target point!"));
                        } else {
                            double speed = 2.5;
                            Vec3 movement = direction.normalize().scale(Math.min(speed, distance));
                            player.teleportTo(playerPos.x + movement.x, playerPos.y + movement.y, playerPos.z + movement.z);
                        }
                    }
                }
            }
        }
    }
}