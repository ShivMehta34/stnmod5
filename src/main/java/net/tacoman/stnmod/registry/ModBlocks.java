package net.tacoman.stnmod.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.blocks.UpgradeTableBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, stnmod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, stnmod.MODID);

    public static final RegistryObject<Block> UPGRADE_TABLE = BLOCKS.register("upgrade_table",
            () -> new UpgradeTableBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE).strength(3.5F).requiresCorrectToolForDrops()));

    public static final RegistryObject<Item> UPGRADE_TABLE_ITEM = ITEMS.register("upgrade_table",
            () -> new BlockItem(UPGRADE_TABLE.get(), new Item.Properties()));

    public static final RegistryObject<Block> OBSIDIAN_BLAST_FURNACE = BLOCKS.register("obsidian_blast_furnace",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.BLAST_FURNACE).strength(50.0F, 1200.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> OBSIDIAN_BLAST_FURNACE_ITEM = ITEMS.register("obsidian_blast_furnace",
            () -> new BlockItem(OBSIDIAN_BLAST_FURNACE.get(), new Item.Properties()));


}
