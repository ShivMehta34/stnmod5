package net.tacoman.stnmod.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.stnmod;

@Mod.EventBusSubscriber(modid = stnmod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CamouflageCooldownProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final ResourceLocation ID = new ResourceLocation(stnmod.MODID, "camouflage_cooldown");

    private final CamouflageCooldown instance = new CamouflageCooldown();
    private final LazyOptional<CamouflageCooldown> optional = LazyOptional.of(() -> instance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return ModCapabilities.CAMOUFLAGE_COOLDOWN.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Cooldown", instance.getCooldown());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.setCooldown(nbt.getLong("Cooldown"));
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Player> event) {
        event.addCapability(ID, new CamouflageCooldownProvider());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(ModCapabilities.CAMOUFLAGE_COOLDOWN).ifPresent(oldCap -> {
            event.getEntity().getCapability(ModCapabilities.CAMOUFLAGE_COOLDOWN).ifPresent(newCap -> {
                newCap.setCooldown(oldCap.getCooldown());
            });
        });
    }
}
