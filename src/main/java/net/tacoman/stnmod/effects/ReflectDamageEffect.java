package net.tacoman.stnmod.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ReflectDamageEffect extends MobEffect {
    public ReflectDamageEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // This makes sure the effect does not apply any action over time
        return false; // No ticking effect, no over-time damage
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Leave this method empty, so it doesn't apply damage over time
    }
}
