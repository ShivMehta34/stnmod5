package net.tacoman.stnmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tacoman.stnmod.handlers.EventHandler;
import net.tacoman.stnmod.items.DaggerItem;
import net.tacoman.stnmod.network.*;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import org.lwjgl.glfw.GLFW;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tacoman.stnmod.handlers.ServerEventHandler;
import net.tacoman.stnmod.items.CustomCrossbowItem;



import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class KeyBindings {
    private static final Logger LOGGER = Logger.getLogger(KeyBindings.class.getName());

    public static final KeyMapping SPECIAL_ABILITY_KEY = new KeyMapping("key.stnmod.special_ability", GLFW.GLFW_KEY_C, "key.categories.gameplay");
    public static final KeyMapping DEFENCE_STANCE_KEY = new KeyMapping("key.stnmod.defence_stance", GLFW.GLFW_KEY_X, "key.categories.gameplay");
    public static final KeyMapping LEG_SHOT_KEY = new KeyMapping("key.stnmod.leg_shot", GLFW.GLFW_KEY_V, "key.categories.gameplay");
    public static final KeyMapping ARROW_RAIN_KEY = new KeyMapping("key.stnmod.arrow_rain", GLFW.GLFW_KEY_B, "key.categories.gameplay");
    public static final KeyMapping Z_KEY = new KeyMapping("key.stnmod.Z", GLFW.GLFW_KEY_Z, "key.categories.gameplay");
    public static final KeyMapping R_KEY = new KeyMapping("key.stnmod.R", GLFW.GLFW_KEY_R, "key.categories.gameplay");
    public static final KeyMapping G_KEY = new KeyMapping("key.stnmod.G", GLFW.GLFW_KEY_G, "key.categories.gameplay");

    private static long lastSummonTime = 0;
    private static long lastBattleStanceTime = 0;
    private static long lastDefenceStanceTime = 0;
    private static long lastLegShotTime = 0;
    private static long lastArrowRainTime = 0;
    private static long lastKillShotTime = 0; // Track the last use of Kill Shot
    private static long lastShieldBashTime = 0; // Track the last use of Shield Bash
    private static long lastThiefAbilityTime = 0; // Track the last use of Thief's special ability
    private static long lastArmorBreakerTime = 0; // Track the last use of Thief's special ability
    private static long lastDefensiveAuraTime = 0; // To track the last time Defensive Aura was used
    private static long lastReinforceShieldTime = 0; // To track the last time Defensive Aura was used
    private static long lastArrowTime = 0;
    private static long lastWhirlwindTime = 0;
    private static long lastDashingStrikeTime = 0;
    private static long lastGrappleTime = 0;
    private static long lastSmokeBombTime= 0;
    private static long lastDoubleFangsTime= 0;
    private static long lastSleepBombTime = 0;
    private static long lastPoisonBombTime = 0;


    private static Entity targetedEntity; // Store the entity targeted by Armor Breaker

    private static final long CLONES_COOLDOWN = 60000; // 1 minute cooldown
    private static final long BATTLE_STANCE_COOLDOWN = 120000; // 2 minutes cooldown
    private static final long DEFENCE_STANCE_COOLDOWN = 120000; // 2 minutes cooldown
    private static final long LEG_SHOT_COOLDOWN = 90000; // 1.5 minutes cooldown
    private static final long ARROW_RAIN_COOLDOWN = 120000; // 2 minutes cooldown
    private static final long KILL_SHOT_COOLDOWN = 300000; // 5 minutes cooldown
    private static final long SHIELD_BASH_COOLDOWN = 40000; // 40 seconds cooldown
    private static final long THIEF_ABILITY_COOLDOWN = 60000; // 1 minute cooldown
    private static final long ARMOR_BREAKER_COOLDOWN = 300000; // 1 minute cooldown
    private static final long DEFENSIVE_AURA_COOLDOWN = 120000; // 2 minutes cooldown
    private static final long REINFORCE_SHIELD_COOLDOWN = 12000; // 2 minutes cooldown
    private static final long ARROW_COOLDOWN = 60000; // 1 minute cooldown
    private static final long WHIRLWIND_COOLDOWN = 12000; // 12s
    private static final long DASHING_STRIKE_COOLDOWN = 20000;
    private static final long GRAPPLE_COOLDOWN = 60000; // 1 minute cooldown
    private static final long SMOKE_BOMB_COOLDOWN = 60000;
    private static final long DOUBLE_FANGS_COOLDOWN = 90000;
    private static final long SLEEP_BOMB_COOLDOWN = 90000;
    private static final long POISON_BOMB_COOLDOWN = 90000;


    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player != null && SPECIAL_ABILITY_KEY.isDown()) {
            long currentTime = System.currentTimeMillis();

            if (player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
                long timeLeft = CLONES_COOLDOWN - (currentTime - lastSummonTime);
                if (timeLeft <= 0) {
                    lastSummonTime = currentTime;
                    LOGGER.info("Special ability key pressed by player (Ninja): " + player.getName().getString());
                    try {
                        NetworkHandler.sendToServer(new SummonClonesPacket());
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                        LOGGER.severe("Error while trying to summon clones: " + e.getMessage());
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before summoning clones again."));
                    LOGGER.info("Cooldown period active. Clones cannot be summoned yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.SAMURAI_STRENGTH.get()) || player.hasEffect(PotionEffectRegistry.SHOGUN_STRENGTH.get())) {
                long timeLeft = BATTLE_STANCE_COOLDOWN - (currentTime - lastBattleStanceTime);
                if (timeLeft <= 0) {
                    lastBattleStanceTime = currentTime;
                    lastDefenceStanceTime = currentTime; // Reset the Defence Stance cooldown
                    LOGGER.info("Special ability key pressed by player: " + player.getName().getString());

                    // Set default duration and amplifier
                    int duration = 20;
                    int amplifier = 0;

                    // Adjust duration and amplifier if the player has Shogun Strength
                    if (player.hasEffect(PotionEffectRegistry.SHOGUN_STRENGTH.get())) {
                        duration = 30;  // Set duration to 30 seconds
                        amplifier = 1;  // Set amplifier to 1
                    }

                    try {
                        NetworkHandler.sendToServer(new ApplyEffectPacket("effect give @s minecraft:strength " + duration + " " + amplifier));
                        NetworkHandler.sendToServer(new ApplyEffectPacket("effect give @s minecraft:speed " + duration + " " + amplifier));

                        player.sendSystemMessage(Component.literal("Battle Stance activated!"));

                        // Schedule reversion after the specified duration
                        scheduler.schedule(() -> {
                            NetworkHandler.sendToServer(new ApplyEffectPacket("effect clear @s minecraft:strength"));
                            NetworkHandler.sendToServer(new ApplyEffectPacket("effect clear @s minecraft:speed"));
                            player.sendSystemMessage(Component.literal("Battle Stance deactivated."));
                        }, duration, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                        LOGGER.severe("Error while trying to activate Battle Stance: " + e.getMessage());
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Battle Stance again."));
                    LOGGER.info("Cooldown period active. Battle Stance cannot be activated yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.SNIPER_STRENGTH.get())) {
                long timeLeft = KILL_SHOT_COOLDOWN - (currentTime - lastKillShotTime);
                if (timeLeft <= 0) {
                    lastKillShotTime = currentTime;
                    LOGGER.info("Kill Shot ability key pressed by player (Sniper): " + player.getName().getString());

                    player.sendSystemMessage(Component.literal("Kill Shot loading..."));

                    // Apply slowness effect for 60 seconds immediately
                    NetworkHandler.sendToServer(new ApplyEffectPacket("effect give @s minecraft:slowness 60 4"));

                    // Countdown timer for 60 seconds until the Kill Shot is ready
                    scheduler.schedule(() -> {
                        player.sendSystemMessage(Component.literal("Kill Shot ready! You have 5 seconds to shoot!"));

                        // Register a new event listener to increase arrow damage
                        Object killShotEventListener = new Object() {
                            @SubscribeEvent
                            public void onEntityJoinWorld(EntityJoinLevelEvent event) {
                                Entity entity = event.getEntity();
                                if (entity instanceof AbstractArrow arrow) {
                                    if (arrow.getOwner() instanceof Player shooter) {
                                        if (shooter.equals(player)) {
                                            arrow.setBaseDamage(arrow.getBaseDamage() * 2.22); // Increase the base damage for the Kill Shot
                                            shooter.sendSystemMessage(Component.literal("Kill Shot activated!"));
                                        }
                                    }
                                }
                            }
                        };
                        MinecraftForge.EVENT_BUS.register(killShotEventListener);

                        // Reverting the damage increase after 5 seconds and ending the slowness effect
                        scheduler.schedule(() -> {
                            player.sendSystemMessage(Component.literal("Kill Shot effect ended."));
                            MinecraftForge.EVENT_BUS.unregister(killShotEventListener);
                            NetworkHandler.sendToServer(new ApplyEffectPacket("effect clear @s minecraft:slowness")); // Clear slowness effect
                        }, 5, TimeUnit.SECONDS);

                    }, 60, TimeUnit.SECONDS);

                    // Single countdown timer
                    for (int i = 1; i <= 12; i++) {
                        int countdown = 60 - (i * 5);
                        scheduler.schedule(() -> {
                            if (countdown > 0) {
                                player.sendSystemMessage(Component.literal("Time left: " + countdown + " seconds"));
                            }
                        }, i * 5, TimeUnit.SECONDS);
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Kill Shot again."));
                    LOGGER.info("Cooldown period active. Kill Shot cannot be activated yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.KNIGHT_STRENGTH.get())) {
                long timeLeft = SHIELD_BASH_COOLDOWN - (currentTime - lastShieldBashTime);
                if (timeLeft <= 0) {
                    lastShieldBashTime = currentTime;
                    LOGGER.info("Shield Bash key pressed by (Knight): " + player.getName().getString());

                    player.sendSystemMessage(Component.literal("Shield Bash ready! Hit something within 3s."));

                    // Arm on the server for a short window
                    NetworkHandler.sendToServer(new ArmShieldBashPacket(60)); // 60 ticks = 3s

                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Shield Bash again."));
                    LOGGER.info("Cooldown period active. Shield Bash cannot be activated yet.");
                }

            } else if (player.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get())) {
                long timeLeft = THIEF_ABILITY_COOLDOWN - (currentTime - lastThiefAbilityTime);
                if (timeLeft <= 0) {
                    lastThiefAbilityTime = currentTime;
                    LOGGER.info("Special ability key pressed by player (Thief): " + player.getName().getString());

                    // Arm on the server: 3s window to backstab (handled server-side)
                    net.tacoman.stnmod.network.NetworkHandler.sendToServer(new net.tacoman.stnmod.network.ArmBackstabPacket());
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "Back-stab armed! Hit a target with a dagger within 3 seconds."));

                    // Optional: schedule nothing (just mirroring your other abilities)
                    scheduler.schedule(() -> { /* noop */ }, THIEF_ABILITY_COOLDOWN, java.util.concurrent.TimeUnit.MILLISECONDS);

                } else {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "Please wait " + (timeLeft / 1000) + " seconds before using your special ability again."));
                    LOGGER.info("Cooldown period active. Thief's special ability cannot be activated yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.ELEMENTAL_RANGER_STRENGTH.get())) {
                long timeLeft = ARROW_COOLDOWN - (currentTime - lastArrowTime);

                if (timeLeft <= 0) {
                    lastArrowTime = currentTime;
                    LOGGER.info("Elemental Ranger ability activated by player: " + player.getName().getString());

                    player.sendSystemMessage(Component.literal("Lightning Shot activated!"));

                    // Send packet to the server to prepare for the lightning arrow
                    NetworkHandler.sendToServer(new TriggerElementalRangerPacket());

                    // Schedule a cooldown message to indicate when the ability is ready again
                    scheduler.schedule(() -> {
                  //      player.sendSystemMessage(Component.literal("Lightning arrow time up."));
                    }, ARROW_COOLDOWN, TimeUnit.MILLISECONDS);
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Elemental Ranger ability again."));
                    LOGGER.info("Cooldown period active. Elemental Ranger ability cannot be activated yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get())) {
                long timeLeft = WHIRLWIND_COOLDOWN - (currentTime - lastWhirlwindTime);

                if (timeLeft <= 0) {
                    lastWhirlwindTime = currentTime;
                    // Fire server packet to start the ability
                    net.tacoman.stnmod.network.NetworkHandler
                            .sendToServer(new net.tacoman.stnmod.network.StartWhirlwindPacket());

// Start local spin visual (only you see this)
                    net.tacoman.stnmod.client.WhirlwindClientSpin.startForMs(3000); // 3s

                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Whirlwind Strike!"));

                    scheduler.schedule(() -> {
                        // optional: uncomment to tell player ability is ready again
                        // player.sendSystemMessage(Component.literal("Whirlwind is ready again."));
                    }, WHIRLWIND_COOLDOWN, java.util.concurrent.TimeUnit.MILLISECONDS);

                } else {
                    player.sendSystemMessage(Component.literal(
                            "Please wait " + (timeLeft / 1000) + " seconds before using Whirlwind again."));
                    LOGGER.info("Cooldown period active. Gladiator Whirlwind cannot be activated yet.");
                }
            }


        }

        if (player != null && G_KEY.isDown()) {
            long currentTime = System.currentTimeMillis();

            if (player.hasEffect(PotionEffectRegistry.MARKSMAN_STRENGTH.get())) {
                long timeLeft = GRAPPLE_COOLDOWN - (currentTime - lastGrappleTime);
                if (timeLeft <= 0) {
                    lastGrappleTime = currentTime;
                    LOGGER.info("Special ability key pressed by player : " + player.getName().getString());
                    try {
                        NetworkHandler.sendToServer(new GrapplePacket());
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                        LOGGER.severe("Error while trying to summon clones: " + e.getMessage());
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before grappling again."));
                    LOGGER.info("Cooldown period active. Clones cannot grapple yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.NIGHTWING_STRENGTH.get())) {
                long timeLeft = POISON_BOMB_COOLDOWN - (currentTime - lastPoisonBombTime);

                if (timeLeft <= 0) {
                    lastPoisonBombTime = currentTime;
                    LOGGER.info("Poison Bomb activated by player (Nightwing): " + player.getName().getString());

                    player.sendSystemMessage(Component.literal("☠ Poison Bomb!"));

                    // Tell the server to apply AoE poison/slow/wither/nausea + green visuals
                    NetworkHandler.sendToServer(new NightwingPoisonBombC2SPacket());

                    // Optional: “ready again” ping later
                    // scheduler.schedule(() -> {}, POISON_BOMB_COOLDOWN, TimeUnit.MILLISECONDS);

                } else {
                    player.sendSystemMessage(Component.literal(
                            "Please wait " + (timeLeft / 1000) + " seconds before using Poison Bomb again."
                    ));
                    LOGGER.info("Cooldown active. Poison Bomb cannot be activated yet.");
                }
            }
        }




        if (player != null && Z_KEY.isDown()) {
            long currentTime = System.currentTimeMillis(); // Declare currentTime here, outside the if-else chain

            if (player.hasEffect(PotionEffectRegistry.ELEMENTAL_RANGER_STRENGTH.get())) {
                long timeLeft = ARROW_COOLDOWN - (currentTime - lastArrowTime);
                if (timeLeft <= 0) {
                    lastArrowTime = currentTime;
                    LOGGER.info("Fire Shot Activated " + player.getName().getString());
                    player.sendSystemMessage(Component.literal("Fire Shot activated!"));

                    // Send packet to the server to prepare for the lightning arrow
                    NetworkHandler.sendToServer(new TriggerFireArrowPacket());

                    // Schedule a cooldown message to indicate when the ability is ready again
                    scheduler.schedule(() -> {
                        //      player.sendSystemMessage(Component.literal("Lightning arrow time up."));
                    }, ARROW_COOLDOWN, TimeUnit.MILLISECONDS);
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Elemental Ranger ability again."));
                    LOGGER.info("Cooldown period active. Elemental Ranger ability cannot be activated yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.NIGHTWING_STRENGTH.get())) {
                long timeLeft = SLEEP_BOMB_COOLDOWN - (currentTime - lastSleepBombTime);

                if (timeLeft <= 0) {
                    lastSleepBombTime = currentTime;
                    LOGGER.info("Sleep Bomb activated by player (Nightwing): " + player.getName().getString());
                    player.sendSystemMessage(Component.literal("Sleep Bomb!"));

                    // Tell the server to apply freeze + purple ring
                    NetworkHandler.sendToServer(new NightwingSleepBombC2SPacket());

                    // Optional “ready again” ping later
                    // scheduler.schedule(() -> player.sendSystemMessage(Component.literal("Sleep Bomb is ready.")),
                    //         SLEEP_BOMB_COOLDOWN, java.util.concurrent.TimeUnit.MILLISECONDS);

                } else {
                    player.sendSystemMessage(Component.literal(
                            "Please wait " + (timeLeft / 1000) + " seconds before using Sleep Bomb again."
                    ));
                    LOGGER.info("Cooldown active. Sleep Bomb cannot be activated yet.");
                }
            }
        }






        // Leg Shot
        if (player != null && LEG_SHOT_KEY.isDown()) {
            long currentTime = System.currentTimeMillis(); // Declare currentTime here, outside the if-else chain

            if (player.hasEffect(PotionEffectRegistry.SNIPER_STRENGTH.get())) {
                long timeLeft = LEG_SHOT_COOLDOWN - (currentTime - lastLegShotTime);
                if (timeLeft <= 0) {
                    lastLegShotTime = currentTime;
                    LOGGER.info("Leg Shot ability key pressed by player (Sniper): " + player.getName().getString());
                    try {
                        NetworkHandler.sendToServer(new LegShotPacket());
                        player.sendSystemMessage(Component.literal("Leg Shot activated!"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                        LOGGER.severe("Error while trying to activate Leg Shot: " + e.getMessage());
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Leg Shot again."));
                    LOGGER.info("Cooldown period active. Leg Shot cannot be activated yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.ELEMENTAL_RANGER_STRENGTH.get())) {
                long timeLeft = ARROW_COOLDOWN - (currentTime - lastArrowTime);

                if (timeLeft <= 0) {
                    lastArrowTime = currentTime;
                    LOGGER.info("Elemental Ranger ability activated by player: " + player.getName().getString());

                    player.sendSystemMessage(Component.literal("Earth Shot activated!"));

                    // Send packet to the server to prepare for the lightning arrow
                    NetworkHandler.sendToServer(new TriggerEarthArrowPacket());

                    // Schedule a cooldown message to indicate when the ability is ready again
                    scheduler.schedule(() -> {
                        //      player.sendSystemMessage(Component.literal("Lightning arrow time up."));
                    }, ARROW_COOLDOWN, TimeUnit.MILLISECONDS);
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Elemental Ranger ability again."));
                    LOGGER.info("Cooldown period active. Elemental Ranger ability cannot be activated yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.KNIGHT_STRENGTH.get())) {
                long timeLeft = DASHING_STRIKE_COOLDOWN - (currentTime - lastDashingStrikeTime);

                if (timeLeft <= 0) {
                    lastDashingStrikeTime = currentTime;
                    LOGGER.info("Dashing Strike activated by player (Knight): " + player.getName().getString());

                    player.sendSystemMessage(Component.literal("⚔ Dashing Strike!"));

                    // Tell the server to perform the dash + line attack
                    NetworkHandler.sendToServer(new StartKnightDashPacket());

                    // Optional: schedule a message when ready again
                    scheduler.schedule(() -> {
                        // player.sendSystemMessage(Component.literal("Dashing Strike is ready again."));
                    }, DASHING_STRIKE_COOLDOWN, TimeUnit.MILLISECONDS);

                } else {
                    player.sendSystemMessage(Component.literal(
                            "Please wait " + (timeLeft / 1000) + " seconds before using Dashing Strike again."
                    ));
                    LOGGER.info("Cooldown period active. Dashing Strike cannot be activated yet.");
                }

            } else if (player.hasEffect(net.tacoman.stnmod.init.PotionEffectRegistry.ASSASSIN_STRENGTH.get())) {
                long timeLeft = DOUBLE_FANGS_COOLDOWN - (currentTime - lastDoubleFangsTime);

                if (timeLeft <= 0) {
                    lastDoubleFangsTime = currentTime;
                    LOGGER.info("Double Fangs activated by player (Assassin): " + player.getName().getString());

                    player.sendSystemMessage(Component.literal("Double Fangs! Leap and strike."));

                    // Try to pass a target id for better leap direction (optional)
                    net.minecraft.world.entity.Entity tgt =
                            net.tacoman.stnmod.client.CrosshairPick.livingUnderCrosshair(8.0D);
                    int id = (tgt != null) ? tgt.getId() : -1;

                    // Tell the server to launch + arm the mid-air finisher
                    NetworkHandler.sendToServer(new DoubleFangsC2SPacket(id));

                    // Optional: ping when ready again
                    scheduler.schedule(() -> {
                        // player.sendSystemMessage(Component.literal("Double Fangs is ready again."));
                    }, DOUBLE_FANGS_COOLDOWN, java.util.concurrent.TimeUnit.MILLISECONDS);

                }
                else {
                    player.sendSystemMessage(Component.literal(
                            "Please wait " + (timeLeft / 1000) + " seconds before using Double Fangs again."
                    ));
                    LOGGER.info("Cooldown active. Double Fangs cannot be activated yet.");
                }

            }else if (player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
                long timeLeft = SMOKE_BOMB_COOLDOWN - (currentTime - lastSmokeBombTime);

                if (timeLeft <= 0) {
                    lastSmokeBombTime = currentTime;
                    LOGGER.info("Smoke Bomb activated by player (Nightwing): " + player.getName().getString());

                    player.sendSystemMessage(Component.literal("Smoke Bomb!"));

                    // Tell the server to spawn the big cloud + blind + teleport
                    NetworkHandler.sendToServer(new NightwingSmokeBombC2SPacket());

                    // Optional: ping when ready again
                    scheduler.schedule(() -> {
                        // player.sendSystemMessage(Component.literal("Smoke Bomb is ready again."));
                    }, SMOKE_BOMB_COOLDOWN, java.util.concurrent.TimeUnit.MILLISECONDS);

                } else {
                    player.sendSystemMessage(Component.literal(
                            "Please wait " + (timeLeft / 1000) + " seconds before using Smoke Bomb again."
                    ));
                    LOGGER.info("Cooldown active. Smoke Bomb cannot be activated yet.");
                }
            }

        }






            if (player != null && DEFENCE_STANCE_KEY.isDown()) {
            long currentTime = System.currentTimeMillis();

            // Samurai Defence Stance
            if (player.hasEffect(PotionEffectRegistry.SAMURAI_STRENGTH.get()) || player.hasEffect(PotionEffectRegistry.SHOGUN_STRENGTH.get())) {
                long timeLeft = DEFENCE_STANCE_COOLDOWN - (currentTime - lastDefenceStanceTime);
                long abilityTimeLeft = BATTLE_STANCE_COOLDOWN - (currentTime - lastBattleStanceTime);
                if (timeLeft <= 0 && abilityTimeLeft <= 0) {
                    lastDefenceStanceTime = currentTime;
                    lastBattleStanceTime = currentTime; // Reset the Battle Stance cooldown
                    LOGGER.info("Defence stance key pressed by player: " + player.getName().getString());

                    // Set default duration and amplifier
                    int duration = 20;
                    int amplifier = 2;

                    // Adjust duration and amplifier if the player has Shogun Strength
                    if (player.hasEffect(PotionEffectRegistry.SHOGUN_STRENGTH.get())) {
                        duration = 30;   // Set duration to 30 seconds
                        amplifier = 3;   // Set amplifier to 3
                    }

                    try {
                        NetworkHandler.sendToServer(new ApplyEffectPacket("effect give @s minecraft:absorption " + duration + " " + amplifier));
                        NetworkHandler.sendToServer(new ApplyEffectPacket("effect give @s minecraft:resistance " + duration + " " + amplifier));

                        player.sendSystemMessage(Component.literal("Defence Stance activated!"));

                        // Schedule reversion after the specified duration
                        scheduler.schedule(() -> {
                            player.sendSystemMessage(Component.literal("Defence Stance deactivated."));
                        }, duration, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                        LOGGER.severe("Error while trying to activate Defence Stance: " + e.getMessage());
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Defence Stance again."));
                    LOGGER.info("Cooldown period active. Defence Stance cannot be activated yet.");
                }
            }
            // Assassin Armor Breaker Ability
            else if (player.hasEffect(PotionEffectRegistry.ASSASSIN_STRENGTH.get())) {
                long timeLeft = ARMOR_BREAKER_COOLDOWN - (currentTime - lastArmorBreakerTime);
                if (timeLeft <= 0) {
                    lastArmorBreakerTime = currentTime;
                    LOGGER.info("Armor Breaker ability key pressed by player (Assassin): " + player.getName().getString());

                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Armor Breaker activated!"));

                    // find a target under crosshair within ~6 blocks
                    net.minecraft.world.entity.Entity tgt =
                            net.tacoman.stnmod.client.CrosshairPick.livingUnderCrosshair(6.0D);

                    if (tgt == null) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Look at an enemy within 6 blocks and press again."));
                    } else {
                        // send C2S packet to run the ability server-side
                        net.tacoman.stnmod.network.NetworkHandler.CHANNEL.sendToServer(
                                new net.tacoman.stnmod.network.ArmorBreakerC2SPacket(tgt.getId())
                        );
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Armor Breaker engaged on " + tgt.getName().getString()));
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Armor Breaker on cooldown for 60 seconds."));
                    }
                } else {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "Please wait " + (timeLeft / 1000) + " seconds before using Armor Breaker again."
                    ));
                    LOGGER.info("Cooldown period active. Armor Breaker cannot be activated yet.");
                }
            } else if (player.hasEffect(PotionEffectRegistry.PALADIN_STRENGTH.get())) {
                long timeLeft = DEFENSIVE_AURA_COOLDOWN - (currentTime - lastDefensiveAuraTime);
                if (timeLeft <= 0) {
                    lastDefensiveAuraTime = currentTime;
                    LOGGER.info("Defensive Aura key pressed by player (Paladin): " + player.getName().getString());

                    try {
                        NetworkHandler.sendToServer(new ApplyEffectPacket("effect give @s minecraft:absorption 20 2"));
                        NetworkHandler.sendToServer(new ApplyEffectPacket("effect give @s minecraft:resistance 20 4"));

                        player.sendSystemMessage(Component.literal("Defensive Aura activated!"));

                        // Schedule reversion after 20 seconds
                        scheduler.schedule(() -> {

                            player.sendSystemMessage(Component.literal("Defensive Aura deactivated."));
                        }, 20, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                        LOGGER.severe("Error while trying to activate Defensive Aura: " + e.getMessage());
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Defensive Aura again."));
                    LOGGER.info("Cooldown period active. Defensive Aura cannot be activated yet.");
                }
            }else if (player.hasEffect(PotionEffectRegistry.ELEMENTAL_RANGER_STRENGTH.get())) {
                long timeLeft = ARROW_COOLDOWN - (currentTime - lastArrowTime);

                if (timeLeft <= 0) {
                    lastArrowTime = currentTime;
                    LOGGER.info("Ice ability activated by player: " + player.getName().getString());

                    player.sendSystemMessage(Component.literal("Ice Shot activated!"));

                    // Send packet to the server to prepare for the lightning arrow
                    NetworkHandler.sendToServer(new TriggerIceArrowPacket());

                    // Schedule a cooldown message to indicate when the ability is ready again
                    scheduler.schedule(() -> {
                        //      player.sendSystemMessage(Component.literal("Lightning arrow time up."));
                    }, ARROW_COOLDOWN, TimeUnit.MILLISECONDS);
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Elemental Ranger ability again."));
                    LOGGER.info("Cooldown period active. Elemental Ranger ability cannot be activated yet.");
                }
            }
        }


        // Marksman Mode Toggle
        if (player != null && R_KEY.isDown()) {
            NetworkHandler.sendToServer(new ToggleMarksmanModePacket());
        }

        // Arrow Rain
        if (player != null && ARROW_RAIN_KEY.isDown()) {
            if (player.hasEffect(PotionEffectRegistry.RANGER_STRENGTH.get())) {
                long currentTime = System.currentTimeMillis();
                long timeLeft = ARROW_RAIN_COOLDOWN - (currentTime - lastArrowRainTime);
                if (timeLeft <= 0) {
                    lastArrowRainTime = currentTime;
                    LOGGER.info("Arrow Rain ability key pressed by player (Ranger): " + player.getName().getString());
                    try {
                        NetworkHandler.sendToServer(new ArrowRainPacket());
                        player.sendSystemMessage(Component.literal("Arrow Rain activated!"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                        LOGGER.severe("Error while trying to activate Arrow Rain: " + e.getMessage());
                    }
                } else {
                    player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Arrow Rain again."));
                    LOGGER.info("Cooldown period active. Arrow Rain cannot be activated yet.");
                }
            }
            else if (player.hasEffect(PotionEffectRegistry.PALADIN_STRENGTH.get())) {
                long currentTime = System.currentTimeMillis();
                    long timeLeft = REINFORCE_SHIELD_COOLDOWN - (currentTime - lastReinforceShieldTime);
                    if (timeLeft <= 0) {
                        lastReinforceShieldTime = currentTime;
                        LOGGER.info("Reinforce Shield key pressed by player (Paladin): " + player.getName().getString());
                        if ((player.getOffhandItem().getItem() instanceof ShieldItem || player.getMainHandItem().getItem() instanceof ShieldItem) && player.isUsingItem())
                            // The player is holding a shield and is blocking
                            try {
                                NetworkHandler.sendToServer(new ApplyEffectPacket("effect give @s stnmod:reflect_damage 20 2"));

                                player.sendSystemMessage(Component.literal("Reinforce Shield activated!"));

                                // Schedule reversion after 20 seconds
                                scheduler.schedule(() -> {
                                    NetworkHandler.sendToServer(new ApplyEffectPacket("effect clear @s stnmod:reflect_damage"));
                                    player.sendSystemMessage(Component.literal("Reinforce Shield deactivated."));
                                }, 20, TimeUnit.SECONDS);
                            } catch (Exception e) {
                                e.printStackTrace();
                                player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                                LOGGER.severe("Error while trying to activate Defensive Aura: " + e.getMessage());
                            }
                        else {
                            player.sendSystemMessage(Component.literal("player not blocking with shield"));
                        }


                    } else {
                        player.sendSystemMessage(Component.literal("Please wait " + (timeLeft / 1000) + " seconds before using Defensive Aura again."));
                        LOGGER.info("Cooldown period active. Defensive Aura cannot be activated yet.");
                    }
                }
            }
        }





    public static void activateArmorBreaker(Player assassin, Entity target) {
        int attacks = 200;
        long interval = 25; // 0.025 seconds between each attack and movement (25 milliseconds)

        // Set invulnerability to the assassin for the entire duration of the ability
        assassin.setInvulnerable(true);

        // Define the offsets for the teleportation pattern
        Vec3[] offsets = new Vec3[]{
                new Vec3(1, 0, 0),  // +1x
                new Vec3(-1, 0, 0), // -1x
                new Vec3(0, 0, 1),  // +1z
                new Vec3(0, 0, -1)  // -1z
        };

        // Schedule each attack and movement
        for (int i = 0; i < attacks; i++) {
            int attackIndex = i;
            Vec3 offset = offsets[i % offsets.length]; // Loop through the motion pattern

            scheduler.schedule(() -> {
                if (assassin.isAlive() && target.isAlive()) {
                    Vec3 targetPos = target.position();

                    // Calculate the relative position for circling
                    double angle = Math.toRadians((360.0 / attacks) * attackIndex);
                    double xOffset = Math.cos(angle) * 1.5; // 1.5 blocks radius
                    double zOffset = Math.sin(angle) * 1.5; // 1.5 blocks radius

                    double targetX = targetPos.x + xOffset;
                    double targetZ = targetPos.z + zOffset;

                    // Find the ground level at the target position
                    BlockPos targetBlockPos = new BlockPos((int) targetX, (int) targetPos.y, (int) targetZ);
                    int groundY = assassin.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, targetBlockPos).getY();

                    // Ensure the assassin teleports to the ground level
                    assassin.teleportTo(targetX, groundY, targetZ);

                    // Recalculate the new position after setting the Y level
                    Vec3 newPos = new Vec3(targetX, groundY, targetZ);
                    double dx = newPos.x - assassin.getX();
                    double dz = newPos.z - assassin.getZ();

                    // Apply consistent motion to simulate circling with a smaller, controlled multiplier
                    assassin.setDeltaMovement(dx * 1.0, 0, dz * 1.0); // Smaller multiplier for controlled speed

                    // Make the assassin face the target
                    double lookDx = targetPos.x - assassin.getX();
                    double lookDy = (targetPos.y + target.getBbHeight() / 2) - (assassin.getY() + assassin.getEyeHeight());
                    double lookDz = targetPos.z - assassin.getZ();

                    float angleYaw = (float) (Math.atan2(lookDz, lookDx) * (180 / Math.PI)) - 90;
                    float anglePitch = (float) (-Math.atan2(lookDy, Math.sqrt(lookDx * lookDx + lookDz * lookDz)) * (180 / Math.PI));

                    assassin.setYRot(angleYaw);
                    assassin.setXRot(anglePitch); // Adjust the pitch to aim at the target's hitbox center

                    // Apply triple damage and perform the attack
                    if (target instanceof LivingEntity livingTarget) {
                        // Apply triple damage buff
                        float originalDamage = assassin.getAttackStrengthScale(0.5F) * 100.0F;
                        assassin.resetAttackStrengthTicker(); // Reset the attack strength cooldown
                        livingTarget.hurt(assassin.damageSources().playerAttack(assassin), originalDamage); // Apply the attack

                        // Apply bleed effect to the target
                        livingTarget.addEffect(new MobEffectInstance(PotionEffectRegistry.BLEED.get(), 100, 1)); // Apply "bleed" effect
                    }
                }
            }, interval * i, TimeUnit.MILLISECONDS);
        }

        // Revert invulnerability after all attacks have been completed
        scheduler.schedule(() -> assassin.setInvulnerable(false), interval * attacks, TimeUnit.MILLISECONDS);
    }






    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SPECIAL_ABILITY_KEY);
        event.register(DEFENCE_STANCE_KEY);
        event.register(LEG_SHOT_KEY);
        event.register(ARROW_RAIN_KEY);
    }
}

