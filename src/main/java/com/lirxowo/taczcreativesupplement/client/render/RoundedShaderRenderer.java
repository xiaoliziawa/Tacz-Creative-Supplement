package com.lirxowo.taczcreativesupplement.client.render;

import com.lirxowo.taczcreativesupplement.Taczcreativesupplement;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import org.joml.Matrix4f;

import java.io.IOException;

public final class RoundedShaderRenderer {

    private static final float DEFAULT_SOFTNESS_PIXELS = 0.9F;
    private static final float MIN_SOFTNESS_GUI = 0.15F;
    private static final ResourceLocation ROUNDED_RECT_SHADER = ResourceLocation.fromNamespaceAndPath(Taczcreativesupplement.MODID, "rounded_rect");

    private static ShaderInstance roundedRectShader;

    private RoundedShaderRenderer() {
    }

    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ROUNDED_RECT_SHADER, DefaultVertexFormat.POSITION), shader -> roundedRectShader = shader);
    }

    public static void fill(GuiGraphics graphics, float x, float y, float width, float height, float radius, int color) {
        fillGradient(graphics, x, y, width, height, radius, DEFAULT_SOFTNESS_PIXELS, color, color);
    }

    public static void fillGradient(GuiGraphics graphics, float x, float y, float width, float height, float radius, int topColor, int bottomColor) {
        fillGradient(graphics, x, y, width, height, radius, DEFAULT_SOFTNESS_PIXELS, topColor, bottomColor);
    }

    public static void fillGradient(GuiGraphics graphics, float x, float y, float width, float height, float radius, float softnessPixels, int topColor, int bottomColor) {
        if (width <= 0.0F || height <= 0.0F) {
            return;
        }

        if (roundedRectShader == null) {
            int x1 = Math.round(x);
            int y1 = Math.round(y);
            int x2 = Math.round(x + width);
            int y2 = Math.round(y + height);
            if (topColor == bottomColor) {
                graphics.fill(x1, y1, x2, y2, topColor);
            } else {
                graphics.fillGradient(x1, y1, x2, y2, topColor, bottomColor);
            }
            return;
        }

        float safeRadius = Math.max(0.0F, Math.min(radius, Math.min(width, height) * 0.5F));
        float safeSoftness = guiUnitsFromPixels(softnessPixels);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ShaderInstance shader = roundedRectShader;
        RenderSystem.setShader(() -> shader);
        shader.safeGetUniform("Rect").set(x, y, width, height);
        shader.safeGetUniform("FillColorTop").set(colorComponent(topColor, 16), colorComponent(topColor, 8), colorComponent(topColor, 0), colorComponent(topColor, 24));
        shader.safeGetUniform("FillColorBottom").set(colorComponent(bottomColor, 16), colorComponent(bottomColor, 8), colorComponent(bottomColor, 0), colorComponent(bottomColor, 24));
        shader.safeGetUniform("Radius").set(safeRadius);
        shader.safeGetUniform("Softness").set(safeSoftness);
        shader.safeGetUniform("ColorModulator").set(1.0F, 1.0F, 1.0F, 1.0F);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(matrix, x, y, 0.0F).endVertex();
        buffer.vertex(matrix, x, y + height, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y + height, 0.0F).endVertex();
        buffer.vertex(matrix, x + width, y, 0.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }

    public static void frame(GuiGraphics graphics, float x, float y, float width, float height, float radius, float thickness, int borderColor, int innerTopColor, int innerBottomColor) {
        fill(graphics, x, y, width, height, radius, borderColor);

        float inset = Math.max(1.0F, thickness);
        float innerWidth = width - inset * 2.0F;
        float innerHeight = height - inset * 2.0F;
        if (innerWidth <= 0.0F || innerHeight <= 0.0F) {
            return;
        }

        fillGradient(graphics, x + inset, y + inset, innerWidth, innerHeight, Math.max(0.0F, radius - inset), innerTopColor, innerBottomColor);
    }

    private static float guiUnitsFromPixels(float physicalPixels) {
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        float scale = (float) Math.max(1.0D, guiScale);
        return Math.max(MIN_SOFTNESS_GUI, physicalPixels / scale);
    }

    private static float colorComponent(int argb, int shift) {
        return ((argb >> shift) & 0xFF) / 255.0F;
    }
}
