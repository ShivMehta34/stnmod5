package net.tacoman.stnmod.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PlayerAnimationController implements GeoAnimatable {
    private final Player player;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PlayerAnimationController(Player player) {
        this.player = player;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // Register your animation controllers here
    }

    @Override
    public double getTick(Object object) {
        return player != null ? player.tickCount : 0;
    }
}
