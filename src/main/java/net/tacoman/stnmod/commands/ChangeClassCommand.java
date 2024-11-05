package net.tacoman.stnmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.tacoman.stnmod.utils.ClassEffectUtils;

public class ChangeClassCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("changeclass")
                .then(Commands.argument("class", StringArgumentType.string())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            // Check if the player is in creative mode


                            String className = StringArgumentType.getString(context, "class");

                            // Clear all effects
                            player.removeAllEffects();


                            // Change class based on the argument
                            switch (className.toLowerCase()) {
                                case "knight":
                                    ClassEffectUtils.setClassKnight(player);
                                    break;
                                case "gladiator":
                                    ClassEffectUtils.setClassGladiator(player);
                                    break;
                                case "ranger":
                                    ClassEffectUtils.setClassRanger(player);
                                    break;
                                case "sniper":
                                    ClassEffectUtils.setClassSniper(player);
                                    break;
                                case "ninja":
                                    ClassEffectUtils.setClassNinja(player);
                                    break;
                                case "samurai":
                                    ClassEffectUtils.setClassSamurai(player);
                                    break;
                                case "thief":
                                    ClassEffectUtils.setClassThief(player);
                                    break;
                                default:
                                    context.getSource().sendFailure(Component.literal("Invalid class name."));
                                    return 0;
                            }


                            return 1;
                        })
                )
        );
    }
}
