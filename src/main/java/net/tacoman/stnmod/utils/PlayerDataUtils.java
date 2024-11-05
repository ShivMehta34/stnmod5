package net.tacoman.stnmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.tacoman.stnmod.client.HUDOverlay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerDataUtils {

    public static final String PERSISTED_NBT_TAG = "stnmod_data";
    public static final String CLASS_TAG = "ChosenClass";
    public static final String LEG_SHOT_TAG = "LegShotActive";
    public static final String BLOOD_LUST_TAG = "BloodLustLevel";
    public static final String RAGE_LEVEL_TAG = "RageLevel"; // New tag for Rage level
    private static final Logger LOGGER = LogManager.getLogger(PlayerDataUtils.class);

    private static int lastBloodLustLevel = -1;
    private static int lastRageLevel = -1; // Track the last rage level for HUD updates
    private static final String SPEED_BOOST_TAG = "SpeedBoost";

    // Player class methods (unchanged)
    public static void setPlayerClass(Player player, String className) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persistentData.putString(CLASS_TAG, className);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentData);
        LOGGER.info("Set player class to " + className);
    }

    public static String getPlayerClass(Player player) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persistentData.getString(CLASS_TAG);
    }

    public static boolean hasPlayerClass(Player player) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persistentData.contains(CLASS_TAG);
    }

    // Leg Shot methods (unchanged)
    public static void setPlayerLegShot(Player player, boolean active) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persistentData.putBoolean(LEG_SHOT_TAG, active);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentData);
        LOGGER.info("Set player leg shot to " + active);
    }

    public static boolean hasPlayerLegShot(Player player) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persistentData.getBoolean(LEG_SHOT_TAG);
    }

    // Blood Lust methods
    public static int getBloodLustLevel(Player player) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persistentData.getInt(BLOOD_LUST_TAG);
    }

    public static void setBloodLustLevel(Player player, int level) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persistentData.putInt(BLOOD_LUST_TAG, level);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentData);
        LOGGER.info("Set Blood Lust level to " + level);
        notifyHUDOverlayBloodLust(player); // Notify HUD for Blood Lust level changes
    }

    public static void incrementBloodLustLevel(Player player, int amount) {
        int currentLevel = getBloodLustLevel(player);
        int newLevel = Math.min(100, currentLevel + amount); // Cap at 100
        setBloodLustLevel(player, newLevel);
        LOGGER.info("Incremented Blood Lust level by " + amount + ". New level: " + newLevel);
    }

    public static void resetBloodLustLevel(Player player) {
        setBloodLustLevel(player, 0);
        LOGGER.info("Reset Blood Lust level to 0");
    }

    private static void notifyHUDOverlayBloodLust(Player player) {
        int currentLevel = getBloodLustLevel(player);
        if (currentLevel != lastBloodLustLevel) {
            lastBloodLustLevel = currentLevel;
            Minecraft.getInstance().execute(() -> HUDOverlay.updateBloodLustLevel(currentLevel));
        }
    }

    // New Rage methods
    public static int getRageLevel(Player player) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persistentData.getInt(RAGE_LEVEL_TAG);
    }

    public static void setRageLevel(Player player, int level) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persistentData.putInt(RAGE_LEVEL_TAG, level);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentData);
        LOGGER.info("Set Rage level to " + level);
        notifyHUDOverlayRage(player); // Notify HUD for Rage level changes
    }

    public static void incrementRageLevel(Player player, float damageTaken) {
        int currentLevel = getRageLevel(player);
        int increment = Math.round(damageTaken); // Round the exact damage amount
        int newLevel = Math.min(960, currentLevel + increment); // Cap rage level at 1000

        setRageLevel(player, newLevel);
        LOGGER.info("Incremented Rage level by damage taken: " + increment + ". New level: " + newLevel);
    }






    public static void resetRageLevel(Player player) {
        setRageLevel(player, 0);
        LOGGER.info("Reset Rage level to 0");
    }



    private static void notifyHUDOverlayRage(Player player) {
        int currentLevel = getRageLevel(player);
        if (currentLevel != lastRageLevel) {
            lastRageLevel = currentLevel;
            Minecraft.getInstance().execute(() -> HUDOverlay.updateRageLevel(currentLevel));
        }
    }
}
