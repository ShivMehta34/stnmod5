package net.tacoman.stnmod.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.utils.PlayerDataUtils;
import net.tacoman.stnmod.utils.ClassEffectUtils;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class PlayerRespawnHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        LOGGER.info("Player respawned: {}", player.getName().getString());
        if (PlayerDataUtils.hasPlayerClass(player)) {
            String playerClass = PlayerDataUtils.getPlayerClass(player);
            LOGGER.info("Applying class effects for class: {}", playerClass);
            applyClassEffects(player, playerClass);
        } else {
            LOGGER.info("No class found for player: {}", player.getName().getString());
        }
    }

    private static void applyClassEffects(Player player, String playerClass) {
        if (player instanceof ServerPlayer serverPlayer) {
            switch (playerClass) {
                case "gladiator_class":
                    ClassEffectUtils.setClassGladiator(serverPlayer);
                    break;
                case "knight_class":
                    ClassEffectUtils.setClassKnight(serverPlayer);
                    break;
                case "ranger_class":
                    ClassEffectUtils.setClassRanger(serverPlayer);
                    break;
                case "sniper_class":
                    ClassEffectUtils.setClassSniper(serverPlayer);
                    break;
                case "thief_class":
                    ClassEffectUtils.setClassThief(serverPlayer);
                    break;
                case "ninja_class":
                    ClassEffectUtils.setClassNinja(serverPlayer);
                    break;
                case "samurai_class":
                    ClassEffectUtils.setClassSamurai(serverPlayer);
                    break;
                default:
                    LOGGER.warn("Unknown class: {}", playerClass);
                    break;
            }
        } else {
            LOGGER.warn("Player is not a ServerPlayer: {}", player.getName().getString());
        }
    }
}
