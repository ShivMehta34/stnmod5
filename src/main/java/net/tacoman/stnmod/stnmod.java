package net.tacoman.stnmod;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tacoman.stnmod.blocks.UpgradeTableBlock;
import net.tacoman.stnmod.client.HUDOverlay;
import net.tacoman.stnmod.client.KeyBindings;
import net.tacoman.stnmod.commands.ChangeClassCommand;
import net.tacoman.stnmod.commands.ClearClassDataCommand;
import net.tacoman.stnmod.commands.CommandRegistrationEvent;
import net.tacoman.stnmod.events.ArrowHitHandler;
import net.tacoman.stnmod.events.ReflectDamageHandler;
import net.tacoman.stnmod.handlers.*;
import net.tacoman.stnmod.init.*;
import net.tacoman.stnmod.network.NetworkHandler;
import net.tacoman.stnmod.events.PlayerRespawnHandler;
import net.tacoman.stnmod.init.stnmodVillagerProfessions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(stnmod.MODID)
public class stnmod {
    public static final String MODID = "stnmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public stnmod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();



        modEventBus.addListener(this::setup);

        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            modEventBus.addListener(this::doClientStuff);
        }

        BlockRegistry.register(modEventBus);
        ITEMS.register(modEventBus);
        ItemRegistry.register(modEventBus);
        PotionEffectRegistry.register(modEventBus);
        EntityRegistry.register(modEventBus);
        stnmodVillagerProfessions.PROFESSIONS.register(modEventBus);
        stnmodVillagerProfessions.POI_TYPES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(ArrowHitHandler.class);
        MinecraftForge.EVENT_BUS.register(stnmodTrades.class);
        MinecraftForge.EVENT_BUS.register(VillagerLevelUpHandler.class);
        MinecraftForge.EVENT_BUS.register(ArmorHandler.class);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(KeyBindings.class);
        MinecraftForge.EVENT_BUS.register(HUDOverlay.class);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new ReflectDamageHandler());



        NetworkHandler.register();

        MinecraftForge.EVENT_BUS.register(PlayerRespawnHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM PREINIT");
        MinecraftForge.EVENT_BUS.addListener(EntityRegistry::registerAttributes);

        MinecraftForge.EVENT_BUS.register(new CommandRegistrationEvent());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        LOGGER.info("HELLO FROM CLIENT SETUP");
        ModItemProperties.register();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        ChangeClassCommand.register(dispatcher);
        ClearClassDataCommand.register(dispatcher);
    }
}
