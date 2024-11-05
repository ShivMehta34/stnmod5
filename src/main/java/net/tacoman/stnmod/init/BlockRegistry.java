package net.tacoman.stnmod.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.blocks.UpgradeTableBlock;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, stnmod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, stnmod.MODID);

    public static final RegistryObject<Block> UPGRADE_TABLE_BLOCK = BLOCKS.register("upgrade_table",
            () -> new UpgradeTableBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)));
    public static final RegistryObject<Item> UPGRADE_TABLE_ITEM = ITEMS.register("upgrade_table",
            () -> new BlockItem(UPGRADE_TABLE_BLOCK.get(), new Item.Properties()));

    // Register Stones Block
    public static final RegistryObject<Block> STONES_BLOCK = BLOCKS.register("obsidian_blast_furnace",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).strength(1.5F, 6.0F)));

    public static final RegistryObject<Item> STONES_BLOCK_ITEM = ITEMS.register("obsidian_blast_furnace",
            () -> new BlockItem(BlockRegistry.STONES_BLOCK.get(), new Item.Properties()));


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
