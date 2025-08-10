// src/main/java/net/tacoman/stnmod/handlers/ShieldBashHandler.java
package net.tacoman.stnmod.handlers;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "stnmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShieldBashHandler {
    // playerId -> armed until serverTick
    private static final Map<UUID, Integer> ARMED_UNTIL = new HashMap<>();

    /** Called by ArmShieldBashPacket on the server */
    public static void arm(ServerPlayer sp, int durationTicks) {
        // Validate class & shield presence server-side
        boolean isKnight = sp.hasEffect(PotionEffectRegistry.KNIGHT_STRENGTH.get());
        boolean hasShield = sp.getOffhandItem().is(Items.SHIELD) || sp.getMainHandItem().is(Items.SHIELD);
        if (!isKnight || !hasShield) {
            sp.sendSystemMessage(Component.literal("You need to be a Knight and have a shield equipped."));
            return;
        }
        int until = sp.server.getTickCount() + Math.max(1, durationTicks);
        ARMED_UNTIL.put(sp.getUUID(), until);
        sp.sendSystemMessage(Component.literal("Shield Bash armed. Hit a target!"));
    }

    /** Consume the armed flag if present and perform bash. */
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return; // server side only
        Integer until = ARMED_UNTIL.get(sp.getUUID());
        if (until == null) return;

        // Still armed?
        int now = sp.server.getTickCount();
        if (now > until) {
            ARMED_UNTIL.remove(sp.getUUID());
            return;
        }

        // Must have a shield at the moment of impact
        boolean hasShield = sp.getOffhandItem().is(Items.SHIELD) || sp.getMainHandItem().is(Items.SHIELD);
        if (!hasShield) {
            ARMED_UNTIL.remove(sp.getUUID());
            return;
        }

        Entity target = e.getTarget();
        // Visuals
        sp.swing(sp.getOffhandItem().is(Items.SHIELD) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, true);
        sp.level().playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);

        // Knockback + debuffs
        Vec3 kb = target.position().subtract(sp.position()).normalize().scale(1.5);
        target.setDeltaMovement(kb);
        if (target instanceof LivingEntity le) {
            le.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 4)); // 3s slowness V
            le.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 255));        // 3s max weakness
        }

        sp.sendSystemMessage(Component.literal("Shield Bash! Target stunned."));

        // consume the armed state so it only triggers once
        ARMED_UNTIL.remove(sp.getUUID());
    }

    /** Housekeeping: expire old entries and avoid leaks between worlds/rejoins. */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.side != LogicalSide.SERVER || e.phase != TickEvent.Phase.END) return;
        int now = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer().getTickCount();
        ARMED_UNTIL.entrySet().removeIf(en -> en.getValue() <= now);
    }
}
