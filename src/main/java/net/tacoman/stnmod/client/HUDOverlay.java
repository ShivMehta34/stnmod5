package net.tacoman.stnmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.init.PotionEffectRegistry;
import net.tacoman.stnmod.stnmod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = stnmod.MODID, value = Dist.CLIENT)
public class HUDOverlay {
    private static final Logger LOGGER = LogManager.getLogger(HUDOverlay.class);
    private static int bloodLustLevel = 0; // For Gladiator
    private static int rageLevel = 0; // For Berserker

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player != null) {
            // Gladiator Bar (Blood Lust) - Display only if Gladiator Strength is active and Berserker Strength is not
            if (player.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get()) &&
                    !player.hasEffect(PotionEffectRegistry.BERSERKER_STRENGTH.get())) {

                int barWidth = 80;
                int barHeight = 5;
                int filledWidth = (int) (bloodLustLevel / 100.0 * barWidth);

                GuiGraphics guiGraphics = event.getGuiGraphics();
                int x = event.getWindow().getGuiScaledWidth() / 2 + 8; // x position
                int y = event.getWindow().getGuiScaledHeight() - 50; // y position above the hunger bar

                // Restore default color
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

                // Draw the blood lust bar
                drawBar(guiGraphics, x, y, barWidth, barHeight, filledWidth, 0xFFFF0000, 0xFF800000);
            }

            // Berserker Bar (Rage) - Display only if Berserker Strength is active
            if (player.hasEffect(PotionEffectRegistry.BERSERKER_STRENGTH.get())) {

                int barWidth = 80;
                int barHeight = 5;
                int filledWidth = (int) (rageLevel / 960.0 * barWidth);

                GuiGraphics guiGraphics = event.getGuiGraphics();
                int x = event.getWindow().getGuiScaledWidth() / 2 + 8; // x position
                int y = event.getWindow().getGuiScaledHeight() - 50; // y position just above the gladiator bar

                // Restore default color
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

                // Draw the rage bar
                drawBar(guiGraphics, x, y, barWidth, barHeight, filledWidth, 0xFF00FF00, 0xFF008000); // Green for rage
            }
        }
    }

    // Helper method to draw bars (reusable for both Gladiator and Berserker)
    private static void drawBar(GuiGraphics guiGraphics, int x, int y, int barWidth, int barHeight, int filledWidth, int colorFilled, int colorEmpty) {
        // Draw the black border around the bar
        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);

        // Draw the gradient-filled part of the bar
        guiGraphics.fillGradient(x, y, x + filledWidth, y + barHeight, colorFilled, colorEmpty);

        // Draw the gradient-empty part of the bar
        guiGraphics.fillGradient(x + filledWidth, y, x + barWidth, y + barHeight, colorEmpty, 0xFF400000);

        // Draw thin black markings every 10 levels
        for (int i = 10; i < 100; i += 10) {
            int markX = x + (i * barWidth / 100);
            guiGraphics.fill(markX, y, markX + 1, y + barHeight, 0xFF000000);
        }

        // Draw rounded corners (left and right)
        guiGraphics.fill(x - 1, y, x, y + barHeight, 0xFF000000); // Left corner
        guiGraphics.fill(x + barWidth, y, x + barWidth + 1, y + barHeight, 0xFF000000); // Right corner
    }

    public static void updateBloodLustLevel(int level) {
        bloodLustLevel = level;
        LOGGER.info("Updated Blood Lust level to " + bloodLustLevel);
    }

    public static void updateRageLevel(int level) {
        rageLevel = level;
        LOGGER.info("Updated Rage level to " + rageLevel);
    }
}
