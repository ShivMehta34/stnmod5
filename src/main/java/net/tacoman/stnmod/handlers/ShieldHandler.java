package net.tacoman.stnmod.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import net.tacoman.stnmod.init.ItemRegistry;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = stnmod.MODID)
public class ShieldHandler {
    private static final UUID SHIELD_SPEED_MODIFIER_UUID = UUID.fromString("5d6f0ba2-9d51-42c3-b40a-98e73cddc872");
    private static final UUID WEAPON_SPEED_MODIFIER_UUID = UUID.fromString("e1234567-89ab-cdef-0123-456789abcdef");

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Check if the equipped or unequipped item is a shield or one of the weapons
        ItemStack newStack = event.getTo();
        ItemStack oldStack = event.getFrom();
        boolean wasShieldOrWeaponEquipped = isShieldOrPenaltyWeapon(oldStack);
        boolean isShieldOrWeaponEquipped = isShieldOrPenaltyWeapon(newStack);

        if (wasShieldOrWeaponEquipped || isShieldOrWeaponEquipped) {
            applyOrRemovePenalties(player, isShieldOrWeaponEquipped, newStack);
        }
    }

    private static boolean isShieldOrPenaltyWeapon(ItemStack stack) {
        return stack.getItem() instanceof ShieldItem ||
                isDagger(stack) ||
                isNinjaSword(stack) ||
                isKatana(stack) ||
                isBoneBasherOrDiamondDestroyer(stack);
    }

    private static boolean isDagger(ItemStack stack) {
        return stack.getItem() == ItemRegistry.STONE_DAGGER.get() ||
                stack.getItem() == ItemRegistry.IRON_DAGGER.get() ||
                stack.getItem() == ItemRegistry.DIAMOND_DAGGER.get() ||
                stack.getItem() == ItemRegistry.NETHERITE_DAGGER.get();
    }

    private static boolean isNinjaSword(ItemStack stack) {
        return stack.getItem() == ItemRegistry.STONE_NINJA_SWORD.get() ||
                stack.getItem() == ItemRegistry.IRON_NINJA_SWORD.get() ||
                stack.getItem() == ItemRegistry.DIAMOND_NINJA_SWORD.get() ||
                stack.getItem() == ItemRegistry.NETHERITE_NINJA_SWORD.get();
    }

    private static boolean isKatana(ItemStack stack) {
        return stack.getItem() == ItemRegistry.STONE_KATANA.get() ||
                stack.getItem() == ItemRegistry.IRON_KATANA.get() ||
                stack.getItem() == ItemRegistry.DIAMOND_KATANA.get() ||
                stack.getItem() == ItemRegistry.NETHERITE_KATANA.get() ||
                stack.getItem() == ItemRegistry.WOODEN_KATANA.get() ||
                stack.getItem() == ItemRegistry.GOLD_KATANA.get() ||
                stack.getItem() == ItemRegistry.KATANA_OF_DIAMOND_BANDITS_GHOST.get();
    }

    private static boolean isBoneBasherOrDiamondDestroyer(ItemStack stack) {
        return stack.getItem() == ItemRegistry.BONE_BASHER.get() ||
                stack.getItem() == ItemRegistry.DIAMOND_DESTROYER.get();
    }

    private static void applyOrRemovePenalties(Player player, boolean isItemEquipped, ItemStack item) {
        boolean isKnightStrength = player.hasEffect(PotionEffectRegistry.KNIGHT_STRENGTH.get());
        boolean isGladiatorStrength = player.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get());
        boolean isThiefStrength = player.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get());
        boolean isSamuraiStrength = player.hasEffect(PotionEffectRegistry.SAMURAI_STRENGTH.get());
        boolean isNinjaStrength = player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get());

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            // Remove any existing speed modifiers
            movementSpeed.removeModifier(SHIELD_SPEED_MODIFIER_UUID);
            movementSpeed.removeModifier(WEAPON_SPEED_MODIFIER_UUID);

            if (isItemEquipped) {
                if (item.getItem() instanceof ShieldItem && !isKnightStrength) {
                    // Apply shield speed penalty for non-Knights
                    AttributeModifier speedPenalty = new AttributeModifier(SHIELD_SPEED_MODIFIER_UUID, "Shield speed penalty", -0.4, AttributeModifier.Operation.MULTIPLY_TOTAL);
                    movementSpeed.addTransientModifier(speedPenalty);
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Integer.MAX_VALUE, 2, false, false));
                } else if (isDagger(item) && !isThiefStrength) {
                    // Apply speed penalty for Thief weapons (daggers)
                    AttributeModifier speedPenalty = new AttributeModifier(WEAPON_SPEED_MODIFIER_UUID, "Dagger speed penalty", -0.3, AttributeModifier.Operation.MULTIPLY_TOTAL);
                    movementSpeed.addTransientModifier(speedPenalty);
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Integer.MAX_VALUE, 2, false, false));
                } else if (isKatana(item) && !isSamuraiStrength) {
                    // Apply speed penalty for Samurai weapons (katanas)
                    AttributeModifier speedPenalty = new AttributeModifier(WEAPON_SPEED_MODIFIER_UUID, "Katana speed penalty", -0.4, AttributeModifier.Operation.MULTIPLY_TOTAL);
                    movementSpeed.addTransientModifier(speedPenalty);
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Integer.MAX_VALUE, 3, false, false));
                } else if (isNinjaSword(item) && !isNinjaStrength) {
                    // Apply speed penalty for Ninja weapons (ninja swords)
                    AttributeModifier speedPenalty = new AttributeModifier(WEAPON_SPEED_MODIFIER_UUID, "Ninja Sword speed penalty", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);
                    movementSpeed.addTransientModifier(speedPenalty);
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Integer.MAX_VALUE, 3, false, false));
                } else if (isBoneBasherOrDiamondDestroyer(item) && !isGladiatorStrength) {
                    // Apply speed penalty for Gladiator weapons (Bone Basher & Diamond Destroyer)
                    AttributeModifier speedPenalty = new AttributeModifier(WEAPON_SPEED_MODIFIER_UUID, "Weapon speed penalty", -0.9, AttributeModifier.Operation.MULTIPLY_TOTAL);
                    movementSpeed.addTransientModifier(speedPenalty);
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Integer.MAX_VALUE, 4, false, false));
                }
            } else {
                // Remove Weakness effect if no penalty weapon is equipped
                player.removeEffect(MobEffects.WEAKNESS);
            }
        }
    }
}
