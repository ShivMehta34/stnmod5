package net.tacoman.stnmod.events;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.tacoman.stnmod.init.PotionEffectRegistry; // Import your effect registry

public class ReflectDamageHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof LivingEntity && entity.getCommandSenderWorld() instanceof ServerLevel) {
            // Check if the entity has the Reflect Damage effect
            if (entity.hasEffect(PotionEffectRegistry.REFLECT_DAMAGE.get())) { // Use .get() to unwrap the RegistryObject
                // Get the attacker (source of the damage)
                LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
                if (attacker != null && attacker.isAlive()) {
                    boolean isBlockingWithShield = isBlocking(entity);

                    // Reflect damage back to the attacker if the player is blocking or not
                    if (isBlockingWithShield) {
                        attacker.hurt(entity.damageSources().magic(), 10.0F); // Reflect 5 hearts (10 points) of damage if blocking
                    } else {
                        //Reflect the same amount of damage back to the attacker
                        float damageReceived = event.getAmount();
                        attacker.hurt(entity.damageSources().magic(), damageReceived);
                    }
                }
                // Prevent the player from taking damage (cancel the event)
                event.setCanceled(true); // This cancels the damage to the player
            }
        }
    }

    // Helper method to check if the player is blocking with a shield
    private boolean isBlocking(LivingEntity entity) {
        ItemStack offHandItem = entity.getOffhandItem();
        ItemStack mainHandItem = entity.getMainHandItem();

        // Check if the entity is using a shield and is actively blocking
        return (offHandItem.getItem() instanceof ShieldItem || mainHandItem.getItem() instanceof ShieldItem) && entity.isUsingItem();
    }
}
