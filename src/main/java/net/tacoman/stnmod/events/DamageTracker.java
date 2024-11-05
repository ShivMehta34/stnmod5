package net.tacoman.stnmod.events;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.UUID;

public class DamageTracker {
    private static final HashMap<UUID, Float> damageMap = new HashMap<>();

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            float damage = event.getAmount();

            // Store the damage taken for the entity using their UUID
            damageMap.put(entity.getUUID(), damage);
        }
    }

    public static float getLastDamage(LivingEntity entity) {
        return damageMap.getOrDefault(entity.getUUID(), 0.0F);
    }

    public static void resetDamage(LivingEntity entity) {
        damageMap.put(entity.getUUID(), 0.0F);
    }
}
