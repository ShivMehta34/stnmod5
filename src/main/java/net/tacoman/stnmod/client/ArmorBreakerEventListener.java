package net.tacoman.stnmod.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class ArmorBreakerEventListener {
    private final Player player;

    public ArmorBreakerEventListener(Player player) {
        this.player = player;
    }

    @SubscribeEvent
    public void onEntityHit(AttackEntityEvent event) {
        if (event.getEntity().equals(player)) {
            Entity target = event.getTarget();
            if (target instanceof LivingEntity) {
                if (player != null && player.isAlive()) {
                    KeyBindings.activateArmorBreaker(player, target);
                    player.sendSystemMessage(Component.literal("Target locked for Armor Breaker!"));
                }
                // Unregister this listener after the attack to prevent multiple triggers
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }
}
