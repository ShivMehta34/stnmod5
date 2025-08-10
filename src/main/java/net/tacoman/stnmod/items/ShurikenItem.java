
package net.tacoman.stnmod.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.tacoman.stnmod.entities.ShurikenEntity;
import net.tacoman.stnmod.init.PotionEffectRegistry;

public class ShurikenItem extends Item {
    // tweakables
    private static final int COOLDOWN_TICKS = 8; // small throw cooldown

    public ShurikenItem(Properties props) { super(props); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Ninja-only gate (swap to your PlayerDataUtils class check if you prefer)
        if (!player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("Only Ninjas can use shurikens."), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            ShurikenEntity shuriken = new ShurikenEntity(level, player);
            shuriken.setItem(stack.copyWithCount(1)); // for renderer
            // shoot with look direction; tune speed + inaccuracy
            var look = player.getLookAngle();
            shuriken.shoot(look.x, look.y, look.z, 2.0f, 0.5f); // speed, inaccuracy
            level.addFreshEntity(shuriken);
        }

        // consume and cooldown
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
