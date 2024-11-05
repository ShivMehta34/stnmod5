package net.tacoman.stnmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tacoman.stnmod.stnmod;

@Mod.EventBusSubscriber(modid = stnmod.MODID, value = Dist.CLIENT)
public class ClassChangeEffectHandler {
    private static final long FADE_DURATION = 5000; // 5 seconds
    private static long fadeStartTime = 0;

    public static void triggerFadeEffect() {
        fadeStartTime = System.currentTimeMillis();
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (fadeStartTime != 0) {
            long elapsedTime = System.currentTimeMillis() - fadeStartTime;
            if (elapsedTime > FADE_DURATION) {
                fadeStartTime = 0;
                return;
            }

            float alpha = 1.0f - (float) elapsedTime / FADE_DURATION;

            Minecraft mc = Minecraft.getInstance();
            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

            int color = 0xFFFFFF | ((int) (255 * alpha) << 24);
            fillGradient(0, 0, width, height, color, color);

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
        float alpha1 = (color1 >> 24 & 255) / 255.0F;
        float red1 = (color1 >> 16 & 255) / 255.0F;
        float green1 = (color1 >> 8 & 255) / 255.0F;
        float blue1 = (color1 & 255) / 255.0F;
        float alpha2 = (color2 >> 24 & 255) / 255.0F;
        float red2 = (color2 >> 16 & 255) / 255.0F;
        float green2 = (color2 >> 8 & 255) / 255.0F;
        float blue2 = (color2 & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(x2, y1, 0).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.vertex(x1, y1, 0).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.vertex(x1, y2, 0).color(red2, green2, blue2, alpha2).endVertex();
        bufferbuilder.vertex(x2, y2, 0).color(red2, green2, blue2, alpha2).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
    }
}
