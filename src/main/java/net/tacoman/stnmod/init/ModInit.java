package net.tacoman.stnmod.init;

import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModInit {
    public static final DamageType DAGGER_DAMAGE_TYPE = new DamageType("dagger", 0.0F);

    public static void init(IEventBus eventBus) {
        eventBus.register(ModInit.class);
        MinecraftForge.EVENT_BUS.register(ModInit.class);
    }

    // Add your other initialization code here
}
