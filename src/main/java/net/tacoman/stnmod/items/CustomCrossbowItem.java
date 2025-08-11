package net.tacoman.stnmod.items;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tacoman.stnmod.handlers.ServerEventHandler;
import net.tacoman.stnmod.init.PotionEffectRegistry;

public class CustomCrossbowItem extends Item {

    public CustomCrossbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // Player can hold indefinitely
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getOrCreateTag().getBoolean("IsCharged")) {
            // Already charged, shoot immediately
            shootArrow(level, player, stack);
            stack.getTag().putBoolean("IsCharged", false);
            return InteractionResultHolder.consume(stack);
        } else {
            // Start charging
            if (player instanceof ServerPlayer serverPlayer) {
                String mode = ServerEventHandler.getMarksmanMode(serverPlayer);
                stack.getOrCreateTag().putString("MarksmanMode", mode);
                stack.getOrCreateTag().putLong("StartTime", level.getGameTime());
                stack.getOrCreateTag().putBoolean("PlayedEndSound", false); // Track if end sound played

                // Play loading start sound
                level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_LOADING_START, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        super.onUseTick(level, entity, stack, count);

        if (!(entity instanceof ServerPlayer player)) return;
        if (!stack.hasTag()) return;
        if (!stack.getTag().contains("MarksmanMode") || !stack.getTag().contains("StartTime")) return;

        String mode = stack.getTag().getString("MarksmanMode");
        long startTime = stack.getTag().getLong("StartTime");
        long elapsed = level.getGameTime() - startTime;

        int requiredTime = switch (mode) {
            case "Quickfire" -> 10;
            case "Concentrate" -> 60;
            case "Penetrate" -> 30;
            default -> 30;
        };

        // Before reaching requiredTime, every 10 ticks play loading middle sound
        if (elapsed < requiredTime) {
            if (elapsed > 0 && elapsed % 10 == 0) {
                level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }

        // Once required time is reached and end sound not played yet, play loading end sound
        if (elapsed >= requiredTime && !stack.getTag().getBoolean("PlayedEndSound")) {
            level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 1.0F, 1.0F);
            // player.sendSystemMessage(Component.literal(mode + " mode: You can release now!"));
            stack.getTag().putBoolean("PlayedEndSound", true);
        }
    }

    private boolean consumeArrow(Player player) {
        // Attempt to find an arrow in the player's inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slotStack = player.getInventory().getItem(i);
            if (!slotStack.isEmpty() && slotStack.getItem() == Items.ARROW) {
                // Consume one arrow
                slotStack.shrink(1);
                if (slotStack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
                return true; // Ammo consumed
            }
        }
        return false; // No ammo found
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        super.releaseUsing(stack, level, entity, timeCharged);

        if (entity instanceof ServerPlayer player && stack.hasTag()) {
            String mode = stack.getTag().getString("MarksmanMode");
            long startTime = stack.getTag().getLong("StartTime");
            long elapsed = level.getGameTime() - startTime;

            int requiredTime = switch (mode) {
                case "Quickfire" -> 10;
                case "Concentrate" -> 60;
                case "Penetrate" -> 30;
                default -> 30;
            };

            if (elapsed >= requiredTime) {
                // Check for ammo before declaring it charged
                if (consumeArrow(player)) {
                    stack.getTag().putBoolean("IsCharged", true);
                    //  player.sendSystemMessage(Component.literal(mode + " mode: Crossbow loaded! Right-click again to fire."));
                } else {
                    //  player.sendSystemMessage(Component.literal("No arrows available to load the crossbow!"));
                }
            } else {
                // player.sendSystemMessage(Component.literal("Not fully charged! Needed: " + requiredTime + " ticks, got: " + elapsed));
            }
        }
    }

    private void shootArrow(Level level, Player player, ItemStack stack) {
        // Check if player has Marksman Strength effect
        boolean isMarksman = player.hasEffect(PotionEffectRegistry.MARKSMAN_STRENGTH.get());

        double damageMultiplier;
        double speedMultiplier;
        byte pierce = 0;

        if (isMarksman) {
            // Marksman damage multipliers
            // Retrieve mode from NBT if desired, or just use fixed values if mode doesn't matter
            String mode = stack.getOrCreateTag().getString("MarksmanMode");

            switch (mode) {
                case "Quickfire" -> {
                    damageMultiplier = 2.22;
                    speedMultiplier = 6.0;
                }
                case "Concentrate" -> {
                    damageMultiplier = 5.0;
                    speedMultiplier = 6.0;
                }
                case "Penetrate" -> {
                    damageMultiplier = 3.0;
                    speedMultiplier = 6.0;
                    pierce = 10;
                }
                default -> {
                    damageMultiplier = 1.0;
                    speedMultiplier = 1.0;
                }
            }
        } else {
            // Non-marksman damage multipliers (significantly lower)
            damageMultiplier = 0.01;
            speedMultiplier = 1.0;
            // No pierce for non-marksman
        }

        Arrow arrow = new Arrow(level, player);
        arrow.setBaseDamage(2.0 * damageMultiplier);

        // Ensure no crits for non-marksman
        if (!isMarksman) {
            arrow.setCritArrow(false);
        }

        Vec3 look = player.getLookAngle();
        arrow.setDeltaMovement(look.scale(speedMultiplier));

        if (pierce > 0) arrow.setPierceLevel(pierce);
        arrow.setOwner(player);
        level.addFreshEntity(arrow);

        level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
        // player.sendSystemMessage(Component.literal("Shot fired! (Marksman: " + isMarksman + ")"));

        stack.getOrCreateTag().putBoolean("IsCharged", false);
    }

}