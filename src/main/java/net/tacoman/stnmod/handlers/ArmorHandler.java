package net.tacoman.stnmod.handlers;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import net.tacoman.stnmod.init.ItemRegistry;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = stnmod.MODID)
public class ArmorHandler {
    private static final UUID ARMOR_SPEED_MODIFIER_UUID = UUID.fromString("d234c3bb-7f5b-4e12-82d4-d3f8334cf0e2");
    private static final UUID WEAPON_SPEED_MODIFIER_UUID = UUID.fromString("e1234567-89ab-cdef-0123-456789abcdef");

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Initialize as false, will change if any incorrect piece of armor is equipped
        boolean shouldApplySpeedPenalty = false;

        // Check each armor slot individually
        shouldApplySpeedPenalty |= checkArmorSlot(player, EquipmentSlot.HEAD);
        shouldApplySpeedPenalty |= checkArmorSlot(player, EquipmentSlot.CHEST);
        shouldApplySpeedPenalty |= checkArmorSlot(player, EquipmentSlot.LEGS);
        shouldApplySpeedPenalty |= checkArmorSlot(player, EquipmentSlot.FEET);

        // If any armor is incorrect, apply the speed penalty
        if (shouldApplySpeedPenalty) {
            applySpeedPenalty(player);
        } else {
            // If no incorrect armor, ensure the penalty is removed
            removeSpeedPenalty(player);
        }

        // Check main hand and off hand for weapons
        checkWeapon(player, player.getMainHandItem());
        checkWeapon(player, player.getOffhandItem());
    }

    private static boolean checkArmorSlot(Player player, EquipmentSlot slot) {
        ItemStack armorPiece = player.getItemBySlot(slot);

        boolean isKnightStrength = player.hasEffect(PotionEffectRegistry.KNIGHT_STRENGTH.get());
        boolean isSamuraiStrength = player.hasEffect(PotionEffectRegistry.SAMURAI_STRENGTH.get());
        boolean isGladiatorStrength = player.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get());
        boolean isNinjaStrength = player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get());
        boolean isThiefStrength = player.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get());

        boolean isRangerOrOtherRestrictedClass = player.hasEffect(PotionEffectRegistry.RANGER_STRENGTH.get()) ||
                player.hasEffect(PotionEffectRegistry.SNIPER_STRENGTH.get()) ||
                isThiefStrength || isNinjaStrength || isGladiatorStrength;

        // Check the armor piece against the allowed classes
        if (armorPiece.getItem() == ItemRegistry.KNIGHT_HELMET.get() || armorPiece.getItem() == ItemRegistry.KNIGHT_CHESTPLATE.get() ||
                armorPiece.getItem() == ItemRegistry.KNIGHT_LEGGINGS.get() || armorPiece.getItem() == ItemRegistry.KNIGHT_BOOTS.get()) {
            return !isKnightStrength;
        } else if (armorPiece.getItem() == ItemRegistry.SAMURAI_HELMET.get() || armorPiece.getItem() == ItemRegistry.SAMURAI_CHESTPLATE.get() ||
                armorPiece.getItem() == ItemRegistry.SAMURAI_LEGGINGS.get() || armorPiece.getItem() == ItemRegistry.SAMURAI_BOOTS.get()) {
            return !isSamuraiStrength;
        } else if (armorPiece.getItem() == ItemRegistry.NINJA_ROBE_HELMET.get() || armorPiece.getItem() == ItemRegistry.NINJA_ROBE_CHESTPLATE.get() ||
                armorPiece.getItem() == ItemRegistry.NINJA_ROBE_LEGGINGS.get() || armorPiece.getItem() == ItemRegistry.NINJA_ROBE_BOOTS.get()) {
            return !isNinjaStrength;
        } else if (armorPiece.getItem() == ItemRegistry.THIEF_CLOAK_HELMET.get() || armorPiece.getItem() == ItemRegistry.THIEF_CLOAK_CHESTPLATE.get() ||
                armorPiece.getItem() == ItemRegistry.THIEF_CLOAK_LEGGINGS.get() || armorPiece.getItem() == ItemRegistry.THIEF_CLOAK_BOOTS.get()) {
            return !isThiefStrength;
        } else if (armorPiece.getItem() == ItemRegistry.DIAMOND_CHAIN_HELMET.get() || armorPiece.getItem() == ItemRegistry.DIAMOND_CHAIN_CHESTPLATE.get() ||
                armorPiece.getItem() == ItemRegistry.DIAMOND_CHAIN_LEGGINGS.get() || armorPiece.getItem() == ItemRegistry.DIAMOND_CHAIN_BOOTS.get()) {
            return !(isKnightStrength || isSamuraiStrength || isGladiatorStrength);
        } else if (armorPiece.getItem() == ItemRegistry.REINFORCED_LEATHER_HELMET.get() || armorPiece.getItem() == ItemRegistry.REINFORCED_LEATHER_CHESTPLATE.get() ||
                armorPiece.getItem() == ItemRegistry.REINFORCED_LEATHER_LEGGINGS.get() || armorPiece.getItem() == ItemRegistry.REINFORCED_LEATHER_BOOTS.get()) {
            return isNinjaStrength || isThiefStrength;
        } else if (isRangerOrOtherRestrictedClass &&
                (armorPiece.getItem() == Items.IRON_HELMET || armorPiece.getItem() == Items.IRON_CHESTPLATE ||
                        armorPiece.getItem() == Items.IRON_LEGGINGS || armorPiece.getItem() == Items.IRON_BOOTS ||
                        armorPiece.getItem() == Items.DIAMOND_HELMET || armorPiece.getItem() == Items.DIAMOND_CHESTPLATE ||
                        armorPiece.getItem() == Items.DIAMOND_LEGGINGS || armorPiece.getItem() == Items.DIAMOND_BOOTS ||
                        armorPiece.getItem() == Items.NETHERITE_HELMET || armorPiece.getItem() == Items.NETHERITE_CHESTPLATE ||
                        armorPiece.getItem() == Items.NETHERITE_LEGGINGS || armorPiece.getItem() == Items.NETHERITE_BOOTS ||
                        armorPiece.getItem() == Items.GOLDEN_HELMET || armorPiece.getItem() == Items.GOLDEN_CHESTPLATE ||
                        armorPiece.getItem() == Items.GOLDEN_LEGGINGS || armorPiece.getItem() == Items.GOLDEN_BOOTS)) {
            return true;
        }

        return false;
    }

    private static void applySpeedPenalty(Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(ARMOR_SPEED_MODIFIER_UUID);
            AttributeModifier speedPenalty = new AttributeModifier(ARMOR_SPEED_MODIFIER_UUID, "Armor speed penalty", -0.4, AttributeModifier.Operation.MULTIPLY_TOTAL);
            movementSpeed.addTransientModifier(speedPenalty);
        }
    }

    private static void removeSpeedPenalty(Player player) {
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(ARMOR_SPEED_MODIFIER_UUID);
        }
    }

    private static void checkWeapon(Player player, ItemStack weapon) {
        // Check if the player is holding the Bone Basher or Diamond Destroyer
        boolean isHoldingPenaltyWeapon = (weapon.getItem() == ItemRegistry.BONE_BASHER.get() || weapon.getItem() == ItemRegistry.DIAMOND_DESTROYER.get());

        // Check if the player has Gladiator Strength
        boolean isGladiatorStrength = player.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get());

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(WEAPON_SPEED_MODIFIER_UUID);

            if (isHoldingPenaltyWeapon && !isGladiatorStrength) {
                // Apply speed penalty
                AttributeModifier speedPenalty = new AttributeModifier(WEAPON_SPEED_MODIFIER_UUID, "Weapon speed penalty", -0.9, AttributeModifier.Operation.MULTIPLY_TOTAL);
                movementSpeed.addTransientModifier(speedPenalty);

                // Apply maximum Weakness effect
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Integer.MAX_VALUE, 3, false, false));
            } else {
                // Remove Weakness effect if the player is not holding the weapon or is a Gladiator
                player.removeEffect(MobEffects.WEAKNESS);
            }
        }
    }
}
