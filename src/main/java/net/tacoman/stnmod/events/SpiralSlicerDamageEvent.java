package net.tacoman.stnmod.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class SpiralSlicerDamageEvent extends Event {
    private final Player player;
    private final LivingEntity target;
    private final float damageAmount;

    public SpiralSlicerDamageEvent(Player player, LivingEntity target, float damageAmount) {
        this.player = player;
        this.target = target;
        this.damageAmount = damageAmount;
    }

    public Player getPlayer() {
        return player;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public float getDamageAmount() {
        return damageAmount;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
