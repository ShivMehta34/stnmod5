package net.tacoman.stnmod.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tacoman.stnmod.effects.BleedEffect;
import net.tacoman.stnmod.potions.NinjaEffect;
import net.tacoman.stnmod.stnmod;
import net.tacoman.stnmod.potions.CustomPotionEffect;

public class PotionEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, stnmod.MODID);

    public static final RegistryObject<MobEffect> RANGER_STRENGTH = MOB_EFFECTS.register("ranger_strength",
            () -> new CustomPotionEffect(MobEffectCategory.BENEFICIAL, 0x98D982)); // Custom color

    public static final RegistryObject<MobEffect> SNIPER_STRENGTH = MOB_EFFECTS.register("sniper_strength",
            () -> new CustomPotionEffect(MobEffectCategory.BENEFICIAL, 0xD98298)); // Custom color

    public static final RegistryObject<MobEffect> THIEF_STRENGTH = MOB_EFFECTS.register("thief_strength",
            () -> new CustomPotionEffect(MobEffectCategory.BENEFICIAL, 0xFFD700)); // Custom color

    public static final RegistryObject<MobEffect> NINJA_STRENGTH = MOB_EFFECTS.register("ninja_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> GLADIATOR_STRENGTH = MOB_EFFECTS.register("gladiator_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> KNIGHT_STRENGTH = MOB_EFFECTS.register("knight_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> SAMURAI_STRENGTH = MOB_EFFECTS.register("samurai_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> ASSASSIN_STRENGTH = MOB_EFFECTS.register("assassin_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> PALADIN_STRENGTH = MOB_EFFECTS.register("paladin_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> BERSERKER_STRENGTH = MOB_EFFECTS.register("berserker_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> SHOGUN_STRENGTH = MOB_EFFECTS.register("shogun_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> ELEMENTAL_RANGER_STRENGTH = MOB_EFFECTS.register("elemental_ranger_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> SAMURAI_BATTLE_STRENGTH = MOB_EFFECTS.register("samurai_battle_strength",
            () -> new NinjaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final RegistryObject<MobEffect> BLEED = MOB_EFFECTS.register("bleed",
            () -> new BleedEffect(MobEffectCategory.HARMFUL, 0x8B0000));
    public static final RegistryObject<MobEffect> REFLECT_DAMAGE = MOB_EFFECTS.register("reflect_damage",
            () -> new BleedEffect(MobEffectCategory.BENEFICIAL, 0x8B0000));




    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
