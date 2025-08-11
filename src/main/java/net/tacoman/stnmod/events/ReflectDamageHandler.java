package net.tacoman.stnmod.events;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import net.tacoman.stnmod.stnmod;

@Mod.EventBusSubscriber(modid = stnmod.MODID)
public class ReflectDamageHandler {

    private static final ThreadLocal<Boolean> REFLECTING = ThreadLocal.withInitial(() -> false);
    private static final float BLOCKING_REFLECT_DAMAGE = 10.0F; // 5 hearts

    /** Reflect AND cancel early so no vanilla hurt flash/sound/knockback happens. */
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onAttack(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim instanceof Player)) return;
        if (!victim.hasEffect(PotionEffectRegistry.REFLECT_DAMAGE.get())) return;
        if (victim.level().isClientSide) return;

        // who hit us?
        Entity src = event.getSource().getEntity();
        if (!(src instanceof LivingEntity attacker) || !attacker.isAlive()) {
            // No valid attacker (e.g., fall/cactus) — just cancel the hit so we stay immune
            event.setCanceled(true);
            return;
        }

        if (REFLECTING.get()) { // safety against loops
            event.setCanceled(true);
            return;
        }

        // How much to reflect: fixed if actively blocking with shield, else mirror incoming amount
        float reflect = isBlocking(victim) ? BLOCKING_REFLECT_DAMAGE : Math.max(0f, event.getAmount());

        try {
            REFLECTING.set(true);

            // Deal the reflected damage with a thorns-like source
            attacker.hurt(victim.damageSources().thorns(victim), reflect);

            // Small pushback on attacker to sell the reflect
            Vec3 kb = attacker.position().subtract(victim.position()).normalize().scale(0.35);
            attacker.setDeltaMovement(attacker.getDeltaMovement().add(kb.x, 0.05, kb.z));
            attacker.hurtMarked = true;

            // One clean shield block sound (server -> everyone)
            victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0f, 1.05f);

        } finally {
            REFLECTING.set(false);
        }

        // Finally, cancel the incoming damage so the player takes none and gets no feedback
        event.setCanceled(true);
    }

    /** Kill knockback while reflect effect is active. */
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onKnockback(LivingKnockBackEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim instanceof Player)) return;
        if (!victim.hasEffect(PotionEffectRegistry.REFLECT_DAMAGE.get())) return;

        event.setStrength(0f);
        event.setRatioX(0f);
        event.setRatioZ(0f);
    }

    private static boolean isBlocking(LivingEntity e) {
        ItemStack off = e.getOffhandItem();
        ItemStack main = e.getMainHandItem();
        boolean hasShield = off.getItem() instanceof ShieldItem || main.getItem() instanceof ShieldItem;
        return hasShield && e.isUsingItem();
    }
}
