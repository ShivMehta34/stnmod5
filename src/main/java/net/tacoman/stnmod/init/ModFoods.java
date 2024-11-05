package net.tacoman.stnmod.init;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    // Define food properties for the Giant Cookie
    public static final FoodProperties GIANT_COOKIE_FOOD = new FoodProperties.Builder()
            .nutrition(16)           // Restores 8 hunger points (4 full icons)
            .saturationMod(1.2F)     // Provides 0.6 saturation
            .build();

    // You can define more custom food items here as needed
}
