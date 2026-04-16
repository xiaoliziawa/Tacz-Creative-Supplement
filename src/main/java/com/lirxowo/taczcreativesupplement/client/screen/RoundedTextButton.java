package com.lirxowo.taczcreativesupplement.client.screen;

import com.lirxowo.taczcreativesupplement.client.render.RoundedShaderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class RoundedTextButton extends AbstractButton {

    @FunctionalInterface
    public interface OnPress {
        void onPress(RoundedTextButton button);
    }

    private static final int OUTER = 0x883A8BFF;
    private static final int INNER_TOP = 0xFF204E96;
    private static final int INNER_BOTTOM = 0xFF18396F;
    private static final int INNER_TOP_HOVER = 0xFF2B63BC;
    private static final int INNER_BOTTOM_HOVER = 0xFF214C90;
    private static final int INNER_TOP_DISABLED = 0xCC2A3142;
    private static final int INNER_BOTTOM_DISABLED = 0xCC222838;
    private static final int TEXT = 0xFFF2F6FF;
    private static final int TEXT_DISABLED = 0xFF97A1B6;

    private final OnPress onPress;

    public RoundedTextButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        onPress.onPress(this);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int x = getX();
        int y = getY();
        int w = width;
        int h = height;

        int innerTop = active ? (isHoveredOrFocused() ? INNER_TOP_HOVER : INNER_TOP) : INNER_TOP_DISABLED;
        int innerBottom = active ? (isHoveredOrFocused() ? INNER_BOTTOM_HOVER : INNER_BOTTOM) : INNER_BOTTOM_DISABLED;
        RoundedShaderRenderer.frame(graphics, x, y, w, h, 10.0F, 1.0F, OUTER, innerTop, innerBottom);

        if (active && isHoveredOrFocused()) {
            RoundedShaderRenderer.fillGradient(graphics, x + 1.0F, y + 1.0F, w - 2.0F, h * 0.45F, 9.0F, 0x22FFFFFF, 0x00000000);
        }

        graphics.drawCenteredString(font, getMessage(), x + w / 2, y + (h - 8) / 2, active ? TEXT : TEXT_DISABLED);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
