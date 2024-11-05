package net.tacoman.stnmod.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.tacoman.stnmod.client.ClassChangeEffectHandler;
import net.tacoman.stnmod.entities.NinjaCloneEntity;
import net.tacoman.stnmod.init.EntityRegistry;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class ClassEffectUtils {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");

    public static void setClassNinja(ServerPlayer player) {
        LOGGER.info("Applying Ninja class effects to player: {}", player.getName().getString());
        player.getAbilities().invulnerable = false;
        player.getAbilities().mayfly = false;

        // Add extra hearts
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(28.0); // 14 hearts

        // Increase walking speed
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
            movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Ninja speed boost", 0.03, AttributeModifier.Operation.ADDITION));
        }

        // Apply Ninja effect for increased digging speed and sneak speed boost
        player.addEffect(new MobEffectInstance(PotionEffectRegistry.NINJA_STRENGTH.get(), Integer.MAX_VALUE, 0, false, false));


        // Summon Ninja clones

        PlayerDataUtils.setPlayerClass(player, "ninja_class");
    }

    private static void summonNinjaClones(ServerPlayer player) {
        Level level = player.getCommandSenderWorld(); // Correct method to get the player's level
        for (int i = 0; i < 2; i++) {
            NinjaCloneEntity clone = new NinjaCloneEntity(EntityRegistry.NINJA_CLONE.get(), level);
            clone.setPos(player.getX() + (i * 2), player.getY(), player.getZ() + (i * 2));
            clone.setOwner(player);
            level.addFreshEntity(clone);
            player.sendSystemMessage(Component.literal("Ninja clone summoned at: " + clone.getX() + ", " + clone.getY() + ", " + clone.getZ()));
        }
    }

    public static void setClassGladiator(ServerPlayer player) {
        LOGGER.info("Applying Gladiator class effects to player: {}", player.getName().getString());
        player.getAbilities().invulnerable = false;
        player.getAbilities().mayfly = false;

        // Add extra hearts
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0); // 15 hearts

        // Increase walking speed
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
            movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Gladiator speed boost", 0.01, AttributeModifier.Operation.ADDITION));
        }

        // Apply custom Gladiator effect for increased damage
        player.addEffect(new MobEffectInstance(PotionEffectRegistry.GLADIATOR_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

    }

    public static void setClassSamurai(ServerPlayer player) {
        LOGGER.info("Applying Samurai class effects to player: {}", player.getName().getString());
        player.getAbilities().invulnerable = false;
        player.getAbilities().mayfly = false;

        // Add extra hearts
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(32.0); // 16 hearts

        // Apply resistance effect
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));

        // Increase walking speed
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
            movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Samurai speed boost", 0.005, AttributeModifier.Operation.ADDITION));
        }

        // Apply custom Samurai effect for increased damage
        player.addEffect(new MobEffectInstance(PotionEffectRegistry.SAMURAI_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

    }

    public static void setClassKnight(ServerPlayer player) {
        LOGGER.info("Applying Knight class effects to player: {}", player.getName().getString());
        player.getAbilities().invulnerable = false;
        player.getAbilities().mayfly = false;

        // Add extra hearts
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0); // 20 hearts

        // Apply custom Knight effect for increased damage
        player.addEffect(new MobEffectInstance(PotionEffectRegistry.KNIGHT_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

        // Apply resistance effect
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));

    }

    public static void setClassRanger(ServerPlayer player) {
        LOGGER.info("Applying Ranger class effects to player: {}", player.getName().getString());
        player.getAbilities().invulnerable = false;
        player.getAbilities().mayfly = false;

        // Add extra hearts
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(28.0); // 14 hearts

        // Apply night vision effect
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));

        // Increase walking speed
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
            movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Ranger speed boost", 0.015, AttributeModifier.Operation.ADDITION));
        }

        // Apply the custom potion effect permanently
        player.addEffect(new MobEffectInstance(PotionEffectRegistry.RANGER_STRENGTH.get(), Integer.MAX_VALUE, 0, false, false));

    }

    public static void setClassSniper(ServerPlayer player) {
        LOGGER.info("Applying Sniper class effects to player: {}", player.getName().getString());
        player.getAbilities().invulnerable = false;
        player.getAbilities().mayfly = false;

        // Add extra hearts
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(26.0); // 13 hearts

        // Increase walking speed
        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
            movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Sniper speed boost", 0, AttributeModifier.Operation.ADDITION));
        }

        // Apply bow damage multiplier
        player.addEffect(new MobEffectInstance(PotionEffectRegistry.SNIPER_STRENGTH.get(), Integer.MAX_VALUE, 0, false, false));

    }

    public static void setClassThief(ServerPlayer player) {
        LOGGER.info("Applying Thief class effects to player: {}", player.getName().getString());
        player.getAbilities().invulnerable = false;
        player.getAbilities().mayfly = false;

        // Add extra hearts
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0); // 12 hearts

        AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
            movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Thief speed boost", 0.035, AttributeModifier.Operation.ADDITION));
        }

        // Apply Haste effect for faster digging
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, Integer.MAX_VALUE, 1, false, false));

        // Apply invisibility while sneaking
        player.addEffect(new MobEffectInstance(PotionEffectRegistry.THIEF_STRENGTH.get(), Integer.MAX_VALUE, 0, false, false));

    }

    private static void giveLongbowBlueprint(ServerPlayer player) {
        ItemStack blueprint = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = blueprint.getOrCreateTag();
        tag.putString("author", "Server");
        tag.putString("title", "Longbow Blueprint");

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Longbow\n\nTo craft a longbow, use the following materials:\n\n- 3 Sticks\n- 3 Strings\n- 1 Iron Ingot"))));
        tag.put("pages", pages);
        blueprint.setTag(tag);
        player.addItem(blueprint);
    }
}
