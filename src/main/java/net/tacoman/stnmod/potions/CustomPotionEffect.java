package net.tacoman.stnmod.potions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.handlers.ServerEventHandler;
import net.tacoman.stnmod.items.CustomCrossbowItem;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = stnmod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CustomPotionEffect extends MobEffect {
    public CustomPotionEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    private static final UUID SNEAK_SPEED_BOOST_UUID = UUID.fromString("c32e9d6a-0ea0-4d55-918d-f13f74fbf9d4");
    private static final AttributeModifier RANGER_SNEAK_SPEED_MODIFIER = new AttributeModifier(SNEAK_SPEED_BOOST_UUID, "Ranger sneak speed boost", 0.06, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SNIPER_SNEAK_SPEED_MODIFIER = new AttributeModifier(SNEAK_SPEED_BOOST_UUID, "Sniper sneak speed boost", 0.06, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier THIEF_SNEAK_SPEED_MODIFIER = new AttributeModifier(SNEAK_SPEED_BOOST_UUID, "Thief sneak speed boost", 0.15, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier NINJA_SNEAK_SPEED_MODIFIER = new AttributeModifier(SNEAK_SPEED_BOOST_UUID, "Ninja sneak speed boost", 0.1, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier ASSASSIN_SNEAK_SPEED_MODIFIER = new AttributeModifier(SNEAK_SPEED_BOOST_UUID, "Assassin sneak speed boost", 0.25, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier ELEMENTAL_RANGER_SNEAK_SPEED_MODIFIER = new AttributeModifier(SNEAK_SPEED_BOOST_UUID, "Elemental Ranger sneak speed boost", 0.12, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier MARKSMAN_SNEAK_SPEED_MODIFIER = new AttributeModifier(SNEAK_SPEED_BOOST_UUID, "Sniper sneak speed boost", 0.01, AttributeModifier.Operation.ADDITION);

    private static final UUID MARKSMAN_SPEED_MODIFIER_UUID = UUID.fromString("e3f6aef1-992c-467f-a8df-7a1a1e34e3f1");


    private static final UUID GLADIATOR_ATTACK_DAMAGE_UUID = UUID.fromString("91AEAA56-376B-4498-935B-2F7F68070635");
    private static final UUID KNIGHT_ATTACK_DAMAGE_UUID = UUID.fromString("55FCED67-E92A-486E-9800-B47F202C4386");
    private static final UUID SAMURAI_ATTACK_DAMAGE_UUID = UUID.fromString("1F145E77-9DB5-4BA6-AAA9-F6F2F5937EDE");
    private static final UUID ASSASSIN_ATTACK_DAMAGE_UUID = UUID.fromString("1F145E77-9DB5-4BA6-AAA9-F6F2F5937EDE");


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof AbstractArrow) {
            AbstractArrow arrow = (AbstractArrow) entity;
            if (arrow.getOwner() instanceof Player) {
                Player shooter = (Player) arrow.getOwner();
                Vec3 originalVelocity = arrow.getDeltaMovement();
                double originalSpeed = originalVelocity.length();

                if (shooter.hasEffect(PotionEffectRegistry.ELEMENTAL_RANGER_STRENGTH.get())) {
                    arrow.setBaseDamage(arrow.getBaseDamage() * 1.2); // Double the base damage for Ranger
                    Vec3 newVelocity = originalVelocity.scale(6.0); // 50% speed increase for Ranger
                    arrow.setDeltaMovement(newVelocity);
                }else if (shooter.hasEffect(PotionEffectRegistry.RANGER_STRENGTH.get())) {
                    arrow.setBaseDamage(arrow.getBaseDamage() * 2.25); // Double the base damage for Ranger
                    Vec3 newVelocity = originalVelocity.scale(2.5); // 50% speed increase for Ranger
                    arrow.setDeltaMovement(newVelocity);
                } else if (shooter.hasEffect(PotionEffectRegistry.SNIPER_STRENGTH.get())) {
                    arrow.setBaseDamage(arrow.getBaseDamage() * 0.44); // Increased damage for Sniper
                    Vec3 newVelocity = originalVelocity.scale(6.0); // 5x speed for Sniper
                    arrow.setDeltaMovement(newVelocity);
                }
            }

        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;

        if (player.hasEffect(PotionEffectRegistry.ELEMENTAL_RANGER_STRENGTH.get())) {
            handleSneakSpeed(player, ELEMENTAL_RANGER_SNEAK_SPEED_MODIFIER);
        } else if (player.hasEffect(PotionEffectRegistry.MARKSMAN_STRENGTH.get())) {
            handleSneakSpeed(player, MARKSMAN_SNEAK_SPEED_MODIFIER);
        } else if (player.hasEffect(PotionEffectRegistry.SNIPER_STRENGTH.get())) {
            handleSneakSpeed(player, SNIPER_SNEAK_SPEED_MODIFIER);
        } else if (player.hasEffect(PotionEffectRegistry.ASSASSIN_STRENGTH.get())) {
            handleSneakSpeed(player, ASSASSIN_SNEAK_SPEED_MODIFIER);
        } else if (player.hasEffect(PotionEffectRegistry.RANGER_STRENGTH.get())) {
            handleSneakSpeed(player, RANGER_SNEAK_SPEED_MODIFIER);
        } else if (player.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get())) {
            handleSneakSpeed(player, THIEF_SNEAK_SPEED_MODIFIER);
            if (player.isCrouching()) {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 2, false, false)); // Triple digging speed
            } else {
                player.removeEffect(MobEffects.INVISIBILITY);
            }
        } else if (player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
            handleSneakSpeed(player, NINJA_SNEAK_SPEED_MODIFIER);
            player.getAbilities().setWalkingSpeed(0.13f); // 30% increased walking speed for Ninja
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 1, false, false)); // Double digging speed for Ninja
        } else if (player.hasEffect(PotionEffectRegistry.ASSASSIN_STRENGTH.get())) {
            handleSneakSpeed(player, ASSASSIN_SNEAK_SPEED_MODIFIER);
            player.getAbilities().setWalkingSpeed(0.13f); // 25% increased walking speed for Assassin
        } else {
            removeSneakSpeedModifier(player, RANGER_SNEAK_SPEED_MODIFIER);
            removeSneakSpeedModifier(player, SNIPER_SNEAK_SPEED_MODIFIER);
            removeSneakSpeedModifier(player, THIEF_SNEAK_SPEED_MODIFIER);
            removeSneakSpeedModifier(player, NINJA_SNEAK_SPEED_MODIFIER);
            removeSneakSpeedModifier(player, ASSASSIN_SNEAK_SPEED_MODIFIER);
            removeSneakSpeedModifier(player, MARKSMAN_SNEAK_SPEED_MODIFIER);
        }

        handleAttackDamage(player);
    }

    private static void handleSneakSpeed(Player player, AttributeModifier modifier) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (player.isCrouching()) {
            if (speedAttribute != null && speedAttribute.getModifier(SNEAK_SPEED_BOOST_UUID) == null) {
                speedAttribute.addTransientModifier(modifier);
            }
        } else {
            if (speedAttribute != null) {
                speedAttribute.removeModifier(SNEAK_SPEED_BOOST_UUID);
            }
        }
    }

    private static void removeSneakSpeedModifier(Player player, AttributeModifier modifier) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(modifier)) {
            speedAttribute.removeModifier(modifier);
        }
    }

    private static void handleAttackDamage(Player player) {
        AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);

        // ---- TIER 2 CLASSES FIRST ----
        if (player.hasEffect(PotionEffectRegistry.BERSERKER_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 10.0) {
                attackDamage.setBaseValue(10.0); // Berserker (T2)
            }
        } else if (player.hasEffect(PotionEffectRegistry.PALADIN_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 4.0) {
                attackDamage.setBaseValue(4.0); // Paladin (T2)
            }
        } else if (player.hasEffect(PotionEffectRegistry.SHOGUN_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 10.0) {
                attackDamage.setBaseValue(10.0); // Shogun (T2)
            }
        } else if (player.hasEffect(PotionEffectRegistry.ASSASSIN_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 4.0) {
                attackDamage.setBaseValue(4.0); // Assassin (T2)
            }
        } else if (player.hasEffect(PotionEffectRegistry.NIGHTWING_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 4.0) {
                attackDamage.setBaseValue(6.0); // Nightwing (T2)
            }

// ---- TIER 1 CLASSES AFTER ----
        } else if (player.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 8.0) {
                attackDamage.setBaseValue(8.0); // Gladiator (T1)
            }
        } else if (player.hasEffect(PotionEffectRegistry.SAMURAI_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 4.0) {
                attackDamage.setBaseValue(4.0); // Samurai (T1)
            }
        } else if (player.hasEffect(PotionEffectRegistry.KNIGHT_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 2.0) {
                attackDamage.setBaseValue(2.0); // Knight (T1)
            }
        } else if (player.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 3.0) {
                attackDamage.setBaseValue(2.0); // Thief (T1)

            }
        } else if (player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
            if (attackDamage != null && attackDamage.getBaseValue() != 3.0) {
                attackDamage.setBaseValue(3.0); // Thief (T1)

            } else {
                if (attackDamage != null) {
                    // Reset the attack damage if it was set by any of the strength effects
                    if (attackDamage.getBaseValue() == 8.0 || attackDamage.getBaseValue() == 2.0 || attackDamage.getBaseValue() == 4.0 || attackDamage.getBaseValue() == 10.0) {
                        attackDamage.setBaseValue(1.0); // Default base value, adjust if necessary
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (player.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get()) && player.isCrouching() && player.hasEffect(MobEffects.INVISIBILITY)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.literal("You cannot attack while crouching and invisible."));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // run only on server to avoid double-effects
        if (player.level().isClientSide) return;

        // --- NINJA: 33% chance to negate and short-teleport ---
        if (player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
            if (Math.random() < 0.33) {
                event.setCanceled(true);
                Vec3 pos = player.position();

                // half distance: -5..+5
                double xOffset = (Math.random() * 10) - 5;
                double zOffset = (Math.random() * 10) - 5;

                int tx = (int) (pos.x + xOffset);
                int ty = (int) pos.y;
                int tz = (int) (pos.z + zOffset);
                BlockPos target = new BlockPos(tx, ty, tz);

                // climb until safe (simple check)
                while (!isSafeTeleportPosition(player.level(), target)) {
                    ty += 1;
                    if (ty > player.level().getMaxBuildHeight()) {
                        tx = (int) pos.x;
                        ty = (int) pos.y;
                        tz = (int) pos.z;
                        target = new BlockPos(tx, ty, tz);
                        break;
                    }
                    target = new BlockPos(tx, ty, tz);
                }

                player.teleportTo(tx + 0.5, ty, tz + 0.5);
            }
            return; // handled Ninja path (regardless of proc)
        }

        // --- THIEF: 60% chance to negate and short-teleport ---
        if (player.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get())) {
            if (Math.random() < 0.60) { // 60% chance
                event.setCanceled(true);
                Vec3 pos = player.position();

                double xOffset = (Math.random() * 5) - 2.5;
                double zOffset = (Math.random() * 5) - 2.5;

                int tx = (int) (pos.x + xOffset);
                int ty = (int) pos.y;
                int tz = (int) (pos.z + zOffset);
                BlockPos target = new BlockPos(tx, ty, tz);

                while (!isSafeTeleportPosition(player.level(), target)) {
                    ty += 1;
                    if (ty > player.level().getMaxBuildHeight()) {
                        tx = (int) pos.x;
                        ty = (int) pos.y;
                        tz = (int) pos.z;
                        target = new BlockPos(tx, ty, tz);
                        break;
                    }
                    target = new BlockPos(tx, ty, tz);
                }

                player.teleportTo(tx + 0.5, ty, tz + 0.5);
            }
            return; // handled Thief path
        }

        // --- ASSASSIN: 85% negate (no teleport) ---
        if (player.hasEffect(PotionEffectRegistry.ASSASSIN_STRENGTH.get())) {
            boolean negate = Math.random() < 0.85; // 85% chance
            if (negate) {
                event.setCanceled(true);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BAMBOO_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            // avoid red flash if source is a mob
            if (event.getSource().getEntity() instanceof Mob) {
                player.hurtMarked = false;
            }
        }
    }

            // Helper method to check if the teleport position is safe
    private static boolean isSafeTeleportPosition(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState stateAbove = level.getBlockState(pos.above());
        return state.getCollisionShape(level, pos).isEmpty() && stateAbove.getCollisionShape(level, pos.above()).isEmpty();
    }
}
