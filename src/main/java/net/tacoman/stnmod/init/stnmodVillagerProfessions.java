package net.tacoman.stnmod.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;

import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.init.BlockRegistry;  // Make sure this is correct and points to where your blocks are registered

import java.util.function.Supplier;
import java.util.function.Predicate;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.ImmutableSet;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class stnmodVillagerProfessions {
    private static final Map<String, ProfessionPoiType> POI_TYPES_MAP = new HashMap<>();
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, stnmod.MODID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, stnmod.MODID);

    public static final RegistryObject<PoiType> BOB_POI = POI_TYPES.register("bob_poi",
            () -> new PoiType(ImmutableSet.copyOf(BlockRegistry.STONES_BLOCK.get().getStateDefinition().getPossibleStates()), 1, 1));

    public static final RegistryObject<VillagerProfession> BOB = PROFESSIONS.register("bob",
            () -> new VillagerProfession("bob",
                    x -> x.get() == BOB_POI.get(),
                    x -> x.get() == BOB_POI.get(),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("ambient.basalt_deltas.mood"))));

    @SubscribeEvent
    public static void registerProfessionPointsOfInterest(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.POI_TYPES, registerHelper -> {
            for (Map.Entry<String, ProfessionPoiType> entry : POI_TYPES_MAP.entrySet()) {
                Block block = entry.getValue().block.get();
                String name = entry.getKey();
                Optional<Holder<PoiType>> existingCheck = PoiTypes.forState(block.defaultBlockState());
                if (existingCheck.isPresent()) {
                    stnmod.LOGGER.error("Skipping villager profession " + name + " that uses POI block " + block + " that is already in use by " + existingCheck);
                    continue;
                }
                PoiType poiType = new PoiType(ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates()), 1, 1);
                registerHelper.register(new ResourceLocation(stnmod.MODID, name), poiType);
                entry.getValue().poiType = ForgeRegistries.POI_TYPES.getHolder(poiType).get();
            }
        });
    }

    private static class ProfessionPoiType {
        final Supplier<Block> block;
        Holder<PoiType> poiType;

        ProfessionPoiType(Supplier<Block> block, Holder<PoiType> poiType) {
            this.block = block;
            this.poiType = poiType;
        }
    }
}
