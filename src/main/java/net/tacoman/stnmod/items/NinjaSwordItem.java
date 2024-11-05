package net.tacoman.stnmod.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NinjaSwordItem extends SwordItem {

    public NinjaSwordItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            if (!player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
                player.sendSystemMessage(Component.literal("You must be a Ninja to use this ninja sword!"));
                return false; // Prevent damage
            }
            return super.hurtEnemy(stack, target, attacker);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.literal("Only usable by Ninjas!"));
    }
}
