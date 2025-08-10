package net.tacoman.stnmod.registry;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tacoman.stnmod.items.RangerBowItem;
import net.tacoman.stnmod.materials.ModArmorMaterials;
import net.tacoman.stnmod.stnmod;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, stnmod.MODID);

    public static final RegistryObject<Item> UPGRADE_TABLE = ITEMS.register("upgrade_table", () -> new BlockItem(ModBlocks.UPGRADE_TABLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> RANGER_BOW = ITEMS.register("ranger_bow",
            () -> new RangerBowItem(new Item.Properties().stacksTo(1).durability(999999)));

    // Register Knight Armor
    public static final RegistryObject<Item> KNIGHT_HELMET = ITEMS.register("knight_helmet",
            () -> new ArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> KNIGHT_CHESTPLATE = ITEMS.register("knight_chestplate",
            () -> new ArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> KNIGHT_LEGGINGS = ITEMS.register("knight_leggings",
            () -> new ArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> KNIGHT_BOOTS = ITEMS.register("knight_boots",
            () -> new ArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.BOOTS, new Item.Properties()));


    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
