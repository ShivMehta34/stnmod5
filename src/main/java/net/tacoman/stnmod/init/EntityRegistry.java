package net.tacoman.stnmod.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tacoman.stnmod.entities.NinjaCloneEntity;
import net.tacoman.stnmod.entities.ShurikenEntity;

import net.tacoman.stnmod.stnmod;

@Mod.EventBusSubscriber(modid = stnmod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, stnmod.MODID);

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, stnmod.MODID);

    public static final RegistryObject<EntityType<NinjaCloneEntity>> NINJA_CLONE = ENTITY_TYPES.register("ninja_clone",
            () -> EntityType.Builder.of(NinjaCloneEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .build(new ResourceLocation(stnmod.MODID, "ninja_clone").toString()));

    // Register your shuriken
    public static final RegistryObject<EntityType<ShurikenEntity>> SHURIKEN =
            ENTITIES.register("shuriken", () ->
                    EntityType.Builder.<ShurikenEntity>of(ShurikenEntity::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(stnmod.MODID + ":shuriken") // OK to use namespaced or just "shuriken"
            );





    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(NINJA_CLONE.get(), NinjaCloneEntity.createAttributes().build());
    }
}
