package net.tacoman.stnmod.materials;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraftforge.registries.ObjectHolder;
import net.tacoman.stnmod.stnmod;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    KNIGHT("knight", 50, new int[]{3, 7, 9, 3}, 15, SoundEvents.ARMOR_EQUIP_DIAMOND, 4.0F, 0.2F, () -> Ingredient.of(Items.DIAMOND)),
    SAMURAI("samurai", 37, new int[]{3, 6, 8, 3}, 15, SoundEvents.ARMOR_EQUIP_IRON, 3.0F, 0.1F, () -> Ingredient.of(Items.IRON_INGOT)),
    REINFORCED_LEATHER("reinforced_leather", 15, new int[]{2, 5, 6, 2}, 25, SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 0.0F, () -> Ingredient.of(Items.LEATHER)),
    NINJA_ROBE("ninja_robe", 20, new int[]{2, 5, 6, 2}, 25, SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 0.0F, () -> Ingredient.of(Items.LEATHER)),
    DIAMOND_CHAIN("diamond_chain", 33, new int[]{3, 6, 8, 3}, 12, SoundEvents.ARMOR_EQUIP_CHAIN, 2.0F, 0.0F, () -> Ingredient.of(Items.CHAIN)),
    THIEF_CLOAK("thief_cloak", 20, new int[]{2, 5, 6, 2}, 25, SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 0.0F, () -> Ingredient.of(Items.LEATHER)),
    PALADIN("paladin", 60, new int[]{3, 8, 10, 3}, 17, SoundEvents.ARMOR_EQUIP_DIAMOND, 4.5F, 0.25F, () -> Ingredient.of(Items.NETHERITE_INGOT));




    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantability;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, int[] slotProtections, int enchantability, SoundEvent sound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.slotProtections = slotProtections;
        this.enchantability = enchantability;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(Type type) {
        return BASE_DURABILITY[type.getSlot().getIndex()] * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(Type type) {
        return this.slotProtections[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return stnmod.MODID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
