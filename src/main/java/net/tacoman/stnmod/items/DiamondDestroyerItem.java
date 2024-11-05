package net.tacoman.stnmod.items;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class DiamondDestroyerItem extends SwordItem {
    public DiamondDestroyerItem(Properties properties) {
        super(new CustomTier(), 10, -3.4F, properties.stacksTo(1));
    }

    // Custom tier class can be created to define durability, damage, etc.
    private static class CustomTier implements Tier {
        @Override
        public int getUses() {
            return 2000; // Custom durability
        }

        @Override
        public float getSpeed() {
            return 6.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 19.0F; // Custom damage
        }

        @Override
        public int getLevel() {
            return 3; // Equivalent to Diamond
        }

        @Override
        public int getEnchantmentValue() {
            return 22; // Enchantment value
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(Items.DIAMOND); // Define repair material
        }
    }
}
