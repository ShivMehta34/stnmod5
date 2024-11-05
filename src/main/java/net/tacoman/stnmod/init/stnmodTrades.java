package net.tacoman.stnmod.init;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.common.BasicItemListing;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.tacoman.stnmod.stnmod;

import java.util.List;

@Mod.EventBusSubscriber(modid = stnmod.MODID)
public class stnmodTrades {
    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == stnmodVillagerProfessions.BOB.get()) {
            // Level 2 Trades
            List<VillagerTrades.ItemListing> level2Trades = event.getTrades().get(2);
            level2Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.IRON_INGOT, 4),
                    new ItemStack(Items.EMERALD, 1),
                    10, 2, 0.02F));
            level2Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(Items.LEATHER, 8), // Second required item
                    new ItemStack(ItemRegistry.REINFORCED_LEATHER_BOOTS.get()),
                    10, 2, 0.02F));
            level2Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(Items.LEATHER, 10), // Second required item
                    new ItemStack(ItemRegistry.REINFORCED_LEATHER_HELMET.get()),
                    10, 2, 0.02F));
            level2Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(Items.IRON_INGOT, 8), // Second required item
                    new ItemStack(ItemRegistry.THIEF_CLOAK_BOOTS.get()),
                    10, 2, 0.02F));
            level2Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(Items.IRON_INGOT, 10), // Second required item
                    new ItemStack(ItemRegistry.THIEF_CLOAK_HELMET.get()),
                    10, 2, 0.02F));

            // Level 3 Trades
            List<VillagerTrades.ItemListing> level3Trades = event.getTrades().get(3);
            level3Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 16),
                    new ItemStack(Items.IRON_INGOT, 16), // Second required item
                    new ItemStack(ItemRegistry.REINFORCED_LEATHER_CHESTPLATE.get()),
                    10, 2, 0.02F));
            level3Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(Items.IRON_INGOT, 14), // Second required item
                    new ItemStack(ItemRegistry.REINFORCED_LEATHER_LEGGINGS.get()),
                    10, 2, 0.02F));
            level3Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 16),
                    new ItemStack(Items.IRON_INGOT, 16), // Second required item
                    new ItemStack(ItemRegistry.THIEF_CLOAK_CHESTPLATE.get()),
                    10, 2, 0.02F));
            level3Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(Items.IRON_INGOT, 14), // Second required item
                    new ItemStack(ItemRegistry.THIEF_CLOAK_LEGGINGS.get()),
                    10, 2, 0.02F));
            level3Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(Items.IRON_INGOT, 8), // Second required item
                    new ItemStack(ItemRegistry.NINJA_ROBE_BOOTS.get()),
                    10, 2, 0.02F));
            level3Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(Items.IRON_INGOT, 10), // Second required item
                    new ItemStack(ItemRegistry.NINJA_ROBE_HELMET.get()),
                    10, 2, 0.02F));
            level3Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 50),
                    new ItemStack(Items.IRON_INGOT, 32), // Second required item
                    new ItemStack(ItemRegistry.BONE_BASHER.get()),
                    10, 2, 0.02F));

            // Level 4 Trades
            List<VillagerTrades.ItemListing> level4Trades = event.getTrades().get(4);
            level4Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 16),
                    new ItemStack(Items.IRON_INGOT, 16), // Second required item
                    new ItemStack(ItemRegistry.NINJA_ROBE_CHESTPLATE.get()),
                    10, 2, 0.02F));
            level4Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(Items.IRON_INGOT, 14), // Second required item
                    new ItemStack(ItemRegistry.NINJA_ROBE_LEGGINGS.get()),
                    10, 2, 0.02F));
            level4Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(Items.IRON_INGOT, 8), // Second required item
                    new ItemStack(ItemRegistry.SAMURAI_BOOTS.get()),
                    10, 2, 0.02F));
            level4Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(Items.IRON_INGOT, 10), // Second required item
                    new ItemStack(ItemRegistry.SAMURAI_HELMET.get()),
                    10, 2, 0.02F));
            level4Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(Items.DIAMOND, 8), // Second required item
                    new ItemStack(ItemRegistry.DIAMOND_CHAIN_BOOTS.get()),
                    10, 2, 0.02F));
            level4Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(Items.DIAMOND, 10), // Second required item
                    new ItemStack(ItemRegistry.DIAMOND_CHAIN_HELMET.get()),
                    10, 2, 0.02F));

            // Level 5 Trades
            List<VillagerTrades.ItemListing> level5Trades = event.getTrades().get(5);

            level5Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 16),
                    new ItemStack(Items.IRON_INGOT, 16), // Second required item
                    new ItemStack(ItemRegistry.SAMURAI_CHESTPLATE.get()),
                    10, 2, 0.02F));

            level5Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD_BLOCK, 32),
                    new ItemStack(Items.DIAMOND_BLOCK, 32), // Second required item
                    new ItemStack(ItemRegistry.DIAMOND_DESTROYER.get()),
                    10, 2, 0.02F));

            level5Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(Items.DIAMOND, 14), // Second required item
                    new ItemStack(ItemRegistry.SAMURAI_LEGGINGS.get()),
                    10, 2, 0.02F));

            level5Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 16),
                    new ItemStack(Items.DIAMOND, 16), // Second required item
                    new ItemStack(ItemRegistry.DIAMOND_CHAIN_CHESTPLATE.get()),
                    10, 2, 0.02F));

            level5Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(Items.DIAMOND, 14), // Second required item
                    new ItemStack(ItemRegistry.DIAMOND_CHAIN_LEGGINGS.get()),
                    10, 2, 0.02F));

            level5Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(Items.IRON_INGOT, 8), // Second required item
                    new ItemStack(ItemRegistry.KNIGHT_BOOTS.get()),
                    10, 2, 0.02F));

            level5Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 10),
                    new ItemStack(Items.IRON_INGOT, 10), // Second required item
                    new ItemStack(ItemRegistry.KNIGHT_HELMET.get()),
                    10, 2, 0.02F));

            level5Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 16),
                    new ItemStack(Items.IRON_INGOT, 16), // Second required item
                    new ItemStack(ItemRegistry.KNIGHT_CHESTPLATE.get()),
                    10, 2, 0.02F));

            level5Trades.add((entity, random) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(Items.IRON_INGOT, 14), // Second required item
                    new ItemStack(ItemRegistry.KNIGHT_LEGGINGS.get()),
                    10, 2, 0.02F));
        }
    }
}