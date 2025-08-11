package net.tacoman.stnmod.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.tacoman.stnmod.entities.ShurikenEntity;
import net.tacoman.stnmod.init.PotionEffectRegistry;

import java.util.List;

public class ShurikenItem extends Item {

    // Tuning knobs
    private static final int  COOLDOWN_TICKS   = 8;     // ~0.4s
    private static final float SHOOT_SPEED     = 2.1f;  // initial speed
    private static final float INACCURACY      = 0.5f;  // slight spread

    public ShurikenItem(Properties props) {
        super(props
                .stacksTo(64)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Ninja-only gate (swap to your PlayerDataUtils if that's the source of truth)
        if (!player.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("Only Ninjas can use shurikens.")
                        .withStyle(ChatFormatting.DARK_PURPLE), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        // Server-side spawn ONLY (this is the key bit)
        if (!level.isClientSide) {
            ShurikenEntity shuriken = new ShurikenEntity(level, player);
            // Optional: carry the item for the ThrownItemRenderer
            shuriken.setItem(stack.copyWithCount(1));

            Vec3 look = player.getLookAngle();
            shuriken.shoot(look.x, look.y, look.z, SHOOT_SPEED, INACCURACY);

            level.addFreshEntity(shuriken);

            // Sound from server so everyone near hears it
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.7f, 1.6f);
        }

        // Consume one (unless creative) and apply a short cooldown (always do this locally)
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        // sidedSuccess gives proper hand animation client-side
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Ninja Only").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("Right-click to throw.").withStyle(ChatFormatting.GRAY));
    }
}
