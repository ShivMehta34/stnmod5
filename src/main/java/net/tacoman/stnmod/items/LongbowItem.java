package net.tacoman.stnmod.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.tacoman.stnmod.init.PotionEffectRegistry;

public class LongbowItem extends BowItem {

    public LongbowItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            boolean hasArrows = player.getAbilities().instabuild || player.getInventory().contains(new ItemStack(Items.ARROW));
            if (hasArrows) {
                float power = getPowerForTime(this.getUseDuration(stack) - timeLeft);
                if (power >= 0.1F) {
                    if (player.getAbilities().instabuild) {
                        power = 1.0F;
                    }

                    Arrow arrow = new Arrow(world, player);
                    arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);

                    if (player.hasEffect(PotionEffectRegistry.SNIPER_STRENGTH.get())) {
                        // Only apply the additional effects if the player is a Sniper
                        arrow.setBaseDamage(arrow.getBaseDamage() * (power * 0.3)); // Scale damage with power for Longbow
                        arrow.setDeltaMovement(arrow.getDeltaMovement().scale(15.0)); // 3x speed for sniper
                    }

                    world.addFreshEntity(arrow);
                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

                    if (!player.getAbilities().instabuild) {
                        ItemStack arrowStack = player.getProjectile(stack);
                        if (!arrowStack.isEmpty()) {
                            arrowStack.shrink(1);
                            if (arrowStack.isEmpty()) {
                                player.getInventory().removeItem(arrowStack);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 720; // 3x the normal draw time
    }

    public static float getPowerForTime(int time) {
        float f = (float) time / 60.0F; // Adjusted to match the longbow's duration
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
}
