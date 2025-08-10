package net.tacoman.stnmod.init;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tacoman.stnmod.blocks.UpgradeTableBlock;
import net.tacoman.stnmod.items.*;
import net.tacoman.stnmod.materials.ModArmorMaterials;
import net.tacoman.stnmod.stnmod;

public class ItemRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, stnmod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, stnmod.MODID);


    public static final RegistryObject<Item> LONGBOW = ITEMS.register("longbow",
            () -> new LongbowItem(new Item.Properties().durability(600)));

    public static final RegistryObject<Item> STONE_DAGGER = ITEMS.register("stone_dagger",
            () -> new DaggerItem(Tiers.STONE, 2, -1.0F, new Item.Properties()));

    public static final RegistryObject<Item> IRON_DAGGER = ITEMS.register("iron_dagger",
            () -> new DaggerItem(Tiers.IRON, 3, -1.0F, new Item.Properties()));

    public static final RegistryObject<Item> DIAMOND_DAGGER = ITEMS.register("diamond_dagger",
            () -> new DaggerItem(Tiers.DIAMOND, 3, -1.0F, new Item.Properties()));

    public static final RegistryObject<Item> NETHERITE_DAGGER = ITEMS.register("netherite_dagger",
            () -> new DaggerItem(Tiers.NETHERITE, 3, -1.0F, new Item.Properties()));

    public static final RegistryObject<Item> STONE_NINJA_SWORD = ITEMS.register("stone_ninja_sword",
            () -> new NinjaSwordItem(Tiers.STONE, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> IRON_NINJA_SWORD = ITEMS.register("iron_ninja_sword",
            () -> new NinjaSwordItem(Tiers.IRON, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_NINJA_SWORD = ITEMS.register("diamond_ninja_sword",
            () -> new NinjaSwordItem(Tiers.DIAMOND, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_NINJA_SWORD = ITEMS.register("netherite_ninja_sword",
            () -> new NinjaSwordItem(Tiers.NETHERITE, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> STONE_KATANA = ITEMS.register("stone_katana",
            () -> new KatanaItem(Tiers.STONE, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> IRON_KATANA = ITEMS.register("iron_katana",
            () -> new KatanaItem(Tiers.IRON, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_KATANA = ITEMS.register("diamond_katana",
            () -> new KatanaItem(Tiers.DIAMOND, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_KATANA = ITEMS.register("netherite_katana",
            () -> new KatanaItem(Tiers.NETHERITE, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> WOODEN_KATANA = ITEMS.register("wooden_katana",
            () -> new KatanaItem(Tiers.WOOD, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> GOLD_KATANA = ITEMS.register("gold_katana",
            () -> new KatanaItem(Tiers.GOLD, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> KATANA_OF_DIAMOND_BANDITS_GHOST = ITEMS.register("katana_of_diamond_bandits_ghost",
            () -> new KatanaItem(Tiers.DIAMOND, 3, -2.0F, new Item.Properties()));
    public static final RegistryObject<Item> GLADIATOR_CLASS = ITEMS.register("gladiator_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Gladiator Class"));

    public static final RegistryObject<Item> KNIGHT_CLASS = ITEMS.register("knight_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Knight Class"));

    public static final RegistryObject<Item> THIEF_CLASS = ITEMS.register("thief_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Thief Class"));

    public static final RegistryObject<Item> NINJA_CLASS = ITEMS.register("ninja_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Ninja Class"));

    public static final RegistryObject<Item> RANGER_CLASS = ITEMS.register("ranger_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Ranger Class"));

    public static final RegistryObject<Item> SNIPER_CLASS = ITEMS.register("sniper_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Sniper Class"));

    public static final RegistryObject<Item> SAMURAI_CLASS = ITEMS.register("samurai_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Samurai Class"));

    public static final RegistryObject<Item> BERSERKER_CLASS = ITEMS.register("berserker_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Berserker Class"));

    public static final RegistryObject<Item> PALADIN_CLASS = ITEMS.register("paladin_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Paladin Class"));

    public static final RegistryObject<Item> ASSASSIN_CLASS = ITEMS.register("assassin_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Assassin Class"));

    public static final RegistryObject<Item> NIGHTWING_CLASS = ITEMS.register("nightwing_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Nightwing Class"));

    public static final RegistryObject<Item> ELEMENTAL_RANGER_CLASS = ITEMS.register("elemental_ranger_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Elemental Ranger Class"));

    public static final RegistryObject<Item> MARKSMAN_CLASS = ITEMS.register("marksman_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Marksman Class"));

    public static final RegistryObject<Item> SHOGUN_CLASS = ITEMS.register("shogun_class",
            () -> new ClassItem(new Item.Properties().rarity(Rarity.RARE), "Shogun Class"));



    public static final RegistryObject<Item> GIANT_COOKIE = ITEMS.register("giant_cookie",
            () -> new Item(new Item.Properties()
                    .food(ModFoods.GIANT_COOKIE_FOOD))); // Reference to the food properties



    public static final RegistryObject<Item> BONE_BASHER = ITEMS.register("bone_basher",
            () -> new BoneBasherItem(new Item.Properties().stacksTo(1).fireResistant()));
    public static final RegistryObject<Item> DIAMOND_DESTROYER = ITEMS.register("diamond_destroyer",
            () -> new DiamondDestroyerItem(new Item.Properties().stacksTo(1).fireResistant()));

    // Register Knight Armor
    public static final RegistryObject<Item> KNIGHT_HELMET = ITEMS.register("knight_helmet",
            () -> new ArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> KNIGHT_CHESTPLATE = ITEMS.register("knight_chestplate",
            () -> new ArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> KNIGHT_LEGGINGS = ITEMS.register("knight_leggings",
            () -> new ArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> KNIGHT_BOOTS = ITEMS.register("knight_boots",
            () -> new ArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Register Diamond Chain
    public static final RegistryObject<Item> DIAMOND_CHAIN_HELMET = ITEMS.register("diamond_chain_helmet",
            () -> new ArmorItem(ModArmorMaterials.DIAMOND_CHAIN, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_CHAIN_CHESTPLATE = ITEMS.register("diamond_chain_chestplate",
            () -> new ArmorItem(ModArmorMaterials.DIAMOND_CHAIN, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_CHAIN_LEGGINGS = ITEMS.register("diamond_chain_leggings",
            () -> new ArmorItem(ModArmorMaterials.DIAMOND_CHAIN, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_CHAIN_BOOTS = ITEMS.register("diamond_chain_boots",
            () -> new ArmorItem(ModArmorMaterials.DIAMOND_CHAIN, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Register Samurai Armor
    public static final RegistryObject<Item> SAMURAI_HELMET = ITEMS.register("samurai_helmet",
            () -> new ArmorItem(ModArmorMaterials.SAMURAI, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> SAMURAI_CHESTPLATE = ITEMS.register("samurai_chestplate",
            () -> new ArmorItem(ModArmorMaterials.SAMURAI, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> SAMURAI_LEGGINGS = ITEMS.register("samurai_leggings",
            () -> new ArmorItem(ModArmorMaterials.SAMURAI, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> SAMURAI_BOOTS = ITEMS.register("samurai_boots",
            () -> new ArmorItem(ModArmorMaterials.SAMURAI, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Register Ninja Robe
    public static final RegistryObject<Item> NINJA_ROBE_HELMET = ITEMS.register("ninja_robe_helmet",
            () -> new ArmorItem(ModArmorMaterials.NINJA_ROBE, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> NINJA_ROBE_CHESTPLATE = ITEMS.register("ninja_robe_chestplate",
            () -> new ArmorItem(ModArmorMaterials.NINJA_ROBE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> NINJA_ROBE_LEGGINGS = ITEMS.register("ninja_robe_leggings",
            () -> new ArmorItem(ModArmorMaterials.NINJA_ROBE, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> NINJA_ROBE_BOOTS = ITEMS.register("ninja_robe_boots",
            () -> new ArmorItem(ModArmorMaterials.NINJA_ROBE, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Register Thief Cloak
    public static final RegistryObject<Item> THIEF_CLOAK_HELMET = ITEMS.register("thief_cloak_helmet",
            () -> new ArmorItem(ModArmorMaterials.THIEF_CLOAK, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> THIEF_CLOAK_CHESTPLATE = ITEMS.register("thief_cloak_chestplate",
            () -> new ArmorItem(ModArmorMaterials.THIEF_CLOAK, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> THIEF_CLOAK_LEGGINGS = ITEMS.register("thief_cloak_leggings",
            () -> new ArmorItem(ModArmorMaterials.THIEF_CLOAK, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> THIEF_CLOAK_BOOTS = ITEMS.register("thief_cloak_boots",
            () -> new ArmorItem(ModArmorMaterials.THIEF_CLOAK, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Register reinforced_leather
    public static final RegistryObject<Item> REINFORCED_LEATHER_HELMET = ITEMS.register("reinforced_leather_helmet",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_LEATHER, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_LEATHER_CHESTPLATE = ITEMS.register("reinforced_leather_chestplate",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_LEATHER, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_LEATHER_LEGGINGS = ITEMS.register("reinforced_leather_leggings",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_LEATHER, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_LEATHER_BOOTS = ITEMS.register("reinforced_leather_boots",
            () -> new ArmorItem(ModArmorMaterials.REINFORCED_LEATHER, ArmorItem.Type.BOOTS, new Item.Properties()));


    // --- Shuriken (Ninja-only item class lives in net.tacoman.stnmod.items) ---
    public static final RegistryObject<Item> SHURIKEN = ITEMS.register("shuriken",
            () -> new net.tacoman.stnmod.items.ShurikenItem(
                    new Item.Properties()
                            .stacksTo(64)
                            .rarity(Rarity.UNCOMMON)
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}