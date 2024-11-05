package net.tacoman.stnmod.handlers;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.utils.PlayerDataUtils;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import org.joml.Vector3f;


import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.x;

@Mod.EventBusSubscriber(modid = stnmod.MODID)
public class EventHandler {
    // Blood Lust UUIDs
    private static final UUID BLOOD_LUST_MODIFIER_UUID = UUID.fromString("7c8d5f04-d9c3-4f89-b8c6-b8c3a939e98a");
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("4c8d5f04-d9c3-4f89-b8c6-b8c3a939e98b");

    // Berserker Rage UUID
    private static final UUID RAGE_MODIFIER_UUID = UUID.fromString("5d8f5f04-d9c3-4f89-b8c6-b8c3a939e98c");

    // Scheduler and logger
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Logger LOGGER = Logger.getLogger(EventHandler.class.getName());

    // Tasks
    private static ScheduledFuture<?> bloodLustDecrementTask;
    private static ScheduledFuture<?> rageDecrementTask;

    // Berserker rage tracking
    private static int rageLevel = 0; // Tracks Berserker rage level
    private static boolean rageFull = false; // Tracks if rage is full and cooldown should occur

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            if (player.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get())) {
                LOGGER.info("Player killed an entity. Incrementing Blood Lust level.");
                PlayerDataUtils.incrementBloodLustLevel(player, 5);
                adjustGladiatorAttributes(player); // Adjust Gladiator's attributes
                resetBloodLustDecrementTask(player);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.hasEffect(PotionEffectRegistry.BERSERKER_STRENGTH.get())) {
                if (!rageFull) { // Only increase rage when it's not full
                    float damageAmount = event.getAmount(); // Get the actual damage taken
                    incrementRageLevel(player, damageAmount * 100); // Increase rage by the damage taken
                    adjustRageAttributes(player); // Adjust Berserker's attributes
                }
            }
        }
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        // Check if the player has Jump Boost effect of level 5 or higher
        if (player.hasEffect(MobEffects.JUMP)) {
            MobEffectInstance jumpBoostEffect = player.getEffect(MobEffects.JUMP);
            if (jumpBoostEffect != null && jumpBoostEffect.getAmplifier() >= 5) {

                // Prevent getting stuck in water by checking if the player is in water
                if (!player.isInWater()) {
                    // Increase fall speed by applying additional downward velocity if the player is falling
                    if (player.getDeltaMovement().y < 0) {
                        player.setDeltaMovement(player.getDeltaMovement().add(0, -0.2, 0)); // Adjust this for fall speed
                    }
                }

                // Increase movement speed while the player is jumping
                if (!player.onGround()) {
                    // Apply horizontal speed boost when airborne
                    double horizontalBoostFactor = 0.05; // Adjust this value to change the boost percentage
                    player.setDeltaMovement(
                            player.getDeltaMovement().add(
                                    player.getDeltaMovement().x * horizontalBoostFactor, // Boost in X direction
                                    0, // Keep Y velocity unchanged
                                    player.getDeltaMovement().z * horizontalBoostFactor // Boost in Z direction
                            )
                    );
                }
            }
        }
    }



    private static void incrementRageLevel(ServerPlayer player, float damageTaken) {
        // Set increment to a fixed amount based on the damage taken
        int increment = Math.round(damageTaken); // Convert damage to an integer for consistent increments

        // Add fixed increment without retaining past increments, capped at 1000
        rageLevel = Math.min(rageLevel + increment, 960);

        // Check if the rage level has reached 1000 to activate Berserker effects
        if (rageLevel == 960 && !rageFull) {
            rageFull = true;
            LOGGER.info("Rage is full! Activating Berserker effects.");
            applyBerserkerEffects(player); // Activate Berserker effects immediately
            resetRageDecrementTask(player); // Start the rage decrement task
        }

        // Update the player's HUD with the new fixed rage level increment
        PlayerDataUtils.setRageLevel(player, rageLevel); // Notify HUD with updated rage level
        LOGGER.info("Incremented Rage level by fixed damage taken: " + increment + ". New level: " + rageLevel);
    }











    private static void applyBerserkerEffects(ServerPlayer player) {
        // Apply extreme Berserker effects
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 19)); // 10x faster
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2400, 19)); // 20x stronger
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 5)); // Resistance 6
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2400, 1)); // Regeneration
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 2400, 19)); // Jump 10x higher

        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 2400)); // White glow


        // Start the rage decrement task
        resetRageDecrementTask(player);
    }

  //  private static void spawnRedParticles(ServerPlayer player) {
   //     if (player.getCommandSenderWorld() instanceof ServerLevel serverLevel) {
     //       // Red color for DUST particles (1.0, 0.0, 0.0 represents RGB red)
       //     float red = 1.0F;
         //   float green = 0.0F;
            //float blue = 0.0F;
          //  float particleSize = 1.0F; // Size of the particles

            // Manually convert to Vector3f by providing the RGB values
   //         Vector3f redColor = new Vector3f(red, green, blue);
//
  //          for (int i = 0; i < 100; i++) { // Spawn more particles for a denser effect
  //              serverLevel.sendParticles(
     //                   new net.minecraft.core.particles.DustParticleOptions(redColor, particleSize),
     //                   player.getX() + (serverLevel.random.nextDouble() - 0.5) * player.getBbWidth(),
      //                  player.getY() + serverLevel.random.nextDouble() * player.getBbHeight(),
    //                    player.getZ() + (serverLevel.random.nextDouble() - 0.5) * player.getBbWidth(),
   //                     10, // Number of particles per tick
  //                      0.5D, 0.5D, 0.5D, // Spread (X, Y, Z)
  //                      0.0D // Speed
  //              );
  //          }
  //      }
  //  }





    private static void resetRageDecrementTask(ServerPlayer player) {
        // Check if rage mode is activated before starting the task
        if (!isRageModeActive(player)) {
            LOGGER.info("Rage mode is not active. No rage decrement task started.");
            return; // Exit if rage mode is not active
        }

        if (rageDecrementTask != null && !rageDecrementTask.isCancelled()) {
            rageDecrementTask.cancel(true);
        }

        // Set the task to apply Berserker effects for exactly 2 minutes, then start decrementing rage
        rageDecrementTask = scheduler.schedule(() -> {
            LOGGER.info("Berserker effects active for 2 minutes. Starting to decrease Rage level.");

            // After 2 minutes, start decrementing rage level for 2 minutes (120 seconds)
            final int[] countdown = {120};  // 120 seconds countdown

            rageDecrementTask = scheduler.scheduleAtFixedRate(() -> {
                if (countdown[0] > 0) {
                    LOGGER.info("Decreasing Rage level for player " + player.getName().getString() + ". Time remaining: " + countdown[0] + " seconds.");
                    decrementRageLevel(player, 8); // Decrement rage each second
                    countdown[0]--;  // Reduce countdown

                    // Optionally spawn particles while rage is active
                    //spawnRedParticles(player);

                } else {
                    LOGGER.info("Rage decrement complete. Removing Berserker effects.");
                    removeBerserkerEffects(player); // Remove Berserker effects after 2 minutes
                    rageDecrementTask.cancel(true);  // Stop the task after 2 minutes
                }
            }, 1, 1, TimeUnit.SECONDS); // Decrease rage every second for 120 seconds

        }, 0, TimeUnit.MINUTES); // Wait for 2 minutes before starting the decrement
    }

    // This is a placeholder function. You will need to implement your own logic for checking if rage mode is active
    private static boolean isRageModeActive(ServerPlayer player) {
        // Logic to determine if rage mode is currently activated
        // Return true if rage mode is active, false otherwise
        return rageFull; // Example: rage mode is active if rage level > 0
    }


    private static void decrementRageLevel(ServerPlayer player, int decrement) {
        rageLevel = Math.max(rageLevel - decrement, 0); // Rage decreases to 0
        PlayerDataUtils.setRageLevel(player, rageLevel); // Update and notify HUD
    }


    private static void removeBerserkerEffects(ServerPlayer player) {
        rageFull = false; // Reset rageFull to allow building up again



        MobEffectInstance resistanceEffect = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
        if (resistanceEffect != null && resistanceEffect.getAmplifier() == 5) {
            player.removeEffect(MobEffects.DAMAGE_RESISTANCE); // Only remove if it's the resistance given by Berserker
        }

        MobEffectInstance regenerationEffect = player.getEffect(MobEffects.REGENERATION);
        if (regenerationEffect != null && regenerationEffect.getAmplifier() == 1) {
            player.removeEffect(MobEffects.REGENERATION); // Only remove if it's the regeneration from Berserker
        }

        MobEffectInstance jumpEffect = player.getEffect(MobEffects.JUMP);
        if (jumpEffect != null && jumpEffect.getAmplifier() == 19) {
            player.removeEffect(MobEffects.JUMP); // Only remove if it's the 10x jump boost from Berserker
        }


        // Remove glowing effect
        player.removeEffect(MobEffects.GLOWING);

        // Stop the decrement task since the rage is depleted
        if (rageDecrementTask != null && !rageDecrementTask.isCancelled()) {
            rageDecrementTask.cancel(true);
        }
    }

    // Adjust Berserker attributes based on rage level
    private static void adjustRageAttributes(Player player) {
        LOGGER.info("Adjusting attributes for Berserker Rage for player " + player.getName().getString());
        // You can adjust attack damage, speed, etc. for the Berserker here
    }

    // Existing Gladiator-related methods for Blood Lust (adjustAttributes renamed to adjustGladiatorAttributes)
    private static void adjustGladiatorAttributes(Player player) {
        LOGGER.info("Adjusting attributes for Gladiator Blood Lust for player " + player.getName().getString());
        adjustAttackDamage(player);
        adjustSpeed(player);
    }

    private static void adjustAttackDamage(Player player) {
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(BLOOD_LUST_MODIFIER_UUID);
            int bloodLustLevel = PlayerDataUtils.getBloodLustLevel(player);
            double additionalDamage = calculateAdditionalDamage(bloodLustLevel);

            AttributeModifier damageModifier = new AttributeModifier(BLOOD_LUST_MODIFIER_UUID, "Blood Lust damage boost", additionalDamage, AttributeModifier.Operation.ADDITION);
            attackDamage.addTransientModifier(damageModifier);
            LOGGER.info("Adjusted attack damage with Blood Lust level " + bloodLustLevel + " adding " + additionalDamage + " damage.");
        }
    }

    private static double calculateAdditionalDamage(int bloodLustLevel) {
        if (bloodLustLevel >= 100) return 6.0;
        if (bloodLustLevel >= 90) return 4.5;
        if (bloodLustLevel >= 80) return 4.0;
        if (bloodLustLevel >= 70) return 3.5;
        if (bloodLustLevel >= 60) return 3.0;
        if (bloodLustLevel >= 50) return 2.5;
        if (bloodLustLevel >= 40) return 2.0;
        if (bloodLustLevel >= 30) return 1.5;
        if (bloodLustLevel >= 20) return 1.0;
        if (bloodLustLevel >= 10) return 0.5;
        return 0.0;
    }

    private static void adjustSpeed(Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
            int bloodLustLevel = PlayerDataUtils.getBloodLustLevel(player);
            double additionalSpeed = calculateAdditionalSpeed(bloodLustLevel);

            AttributeModifier speedModifier = new AttributeModifier(SPEED_MODIFIER_UUID, "Blood Lust speed boost", additionalSpeed, AttributeModifier.Operation.ADDITION);
            movementSpeed.addTransientModifier(speedModifier);
            LOGGER.info("Adjusted movement speed with Blood Lust level " + bloodLustLevel + " adding " + additionalSpeed + " speed.");
        }
    }

    private static double calculateAdditionalSpeed(int bloodLustLevel) {
        if (bloodLustLevel >= 100) return 0.02;
        if (bloodLustLevel >= 90) return 0.018;
        if (bloodLustLevel >= 80) return 0.016;
        if (bloodLustLevel >= 70) return 0.014;
        if (bloodLustLevel >= 60) return 0.012;
        if (bloodLustLevel >= 50) return 0.01;
        if (bloodLustLevel >= 40) return 0.008;
        if (bloodLustLevel >= 30) return 0.006;
        if (bloodLustLevel >= 20) return 0.004;
        if (bloodLustLevel >= 10) return 0.002;
        return 0.0;
    }

    private static void resetBloodLustDecrementTask(ServerPlayer player) {
        if (bloodLustDecrementTask != null && !bloodLustDecrementTask.isCancelled()) {
            bloodLustDecrementTask.cancel(true);
        }

        bloodLustDecrementTask = scheduler.scheduleAtFixedRate(() -> {
            if (player.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get()) && PlayerDataUtils.getBloodLustLevel(player) > 0) {
                LOGGER.info("Decreasing Blood Lust level for player " + player.getName().getString());
                PlayerDataUtils.incrementBloodLustLevel(player, -1);
                adjustGladiatorAttributes(player);
            }
        }, 60, 1, TimeUnit.SECONDS);
    }

    @SubscribeEvent
    public static void onPlayerUseItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getItemStack();
        Player player = event.getEntity();

        if (!player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld() instanceof ServerLevel serverLevel) {
            if (itemStack.getItem() == Items.MILK_BUCKET) {
                // Prevent milk from removing effects
                event.setCanceled(true);

                // Optionally restore hunger
                player.getFoodData().eat(6, 0.6F); // Restore hunger
                itemStack.shrink(1); // Consume milk bucket
                player.addItem(new ItemStack(Items.BUCKET)); // Give empty bucket
            }
        }
    }
}
