package com.lirxowo.taczcreativesupplement.client.screen;

import com.lirxowo.taczcreativesupplement.client.render.RoundedShaderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ToggleButton extends AbstractButton {

    @FunctionalInterface
    public interface OnPress {
        void onPress(ToggleButton button);
    }

    private static final int PILL_W = 52;
    private static final int PILL_H = 22;
    private static final int KNOB_MARGIN = 3;

    private static final int CARD_OUTLINE = 0x585DB5FF;
    private static final int CARD_OUTLINE_HOVER = 0x8A7FD0FF;
    private static final int CARD_TOP = 0x26161F2E;
    private static final int CARD_BOTTOM = 0x20101928;
    private static final int CARD_TOP_HOVER = 0x32213146;
    private static final int CARD_BOTTOM_HOVER = 0x26152034;
    private static final int PILL_ON_TOP = 0xFF2D6ECC;
    private static final int PILL_ON_BOT = 0xFF1A4FA0;
    private static final int PILL_OFF_TOP = 0xFF2B2B3A;
    private static final int PILL_OFF_BOT = 0xFF1E1E2C;
    private static final int KNOB_ON = 0xFFDDEEFF;
    private static final int KNOB_OFF = 0xFF7788AA;
    private static final int TEXT_ACTIVE = 0xFFEEEEFF;
    private static final int TEXT_INACTIVE = 0xFF888899;

    private boolean toggled;
    private final OnPress onPress;

    public ToggleButton(int x, int y, int width, int height, Component label, OnPress onPress, boolean initial) {
        super(x, y, width, height, label);
        this.onPress = onPress;
        this.toggled = initial;
    }

    public void setToggled(boolean value) {
        this.toggled = value;
    }

    public boolean isToggled() {
        return toggled;
    }

    @Override
    public void onPress() {
        onPress.onPress(this);
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
        Font font = Minecraft.getInstance().font;
        int x = getX();
        int y = getY();
        int w = width;
        int h = height;
        int border = isHoveredOrFocused() ? CARD_OUTLINE_HOVER : CARD_OUTLINE;
        int topBg = isHoveredOrFocused() ? CARD_TOP_HOVER : CARD_TOP;
        int bottomBg = isHoveredOrFocused() ? CARD_BOTTOM_HOVER : CARD_BOTTOM;

        RoundedShaderRenderer.frame(g, x, y, w, h, 12.0F, 1.0F, border, topBg, bottomBg);
        if (isHoveredOrFocused()) {
            RoundedShaderRenderer.fillGradient(g, x + 1.0F, y + 1.0F, w - 2.0F, h * 0.45F, 11.0F, 0x20FFFFFF, 0x00000000);
        }

        int labelColor = isActive() ? TEXT_ACTIVE : TEXT_INACTIVE;
        int labelY = y + (h - 8) / 2;
        g.drawString(font, getMessage(), x + 12, labelY, labelColor, false);

        int pillX = x + w - PILL_W - 10;
        int pillY = y + (h - PILL_H) / 2;
        int pillTop = toggled ? PILL_ON_TOP : PILL_OFF_TOP;
        int pillBottom = toggled ? PILL_ON_BOT : PILL_OFF_BOT;
        RoundedShaderRenderer.frame(g, pillX, pillY, PILL_W, PILL_H, PILL_H / 2.0F, 1.0F, 0x38FFFFFF, pillTop, pillBottom);

        int knobSize = PILL_H - KNOB_MARGIN * 2;
        int knobX = toggled ? pillX + PILL_W - knobSize - KNOB_MARGIN : pillX + KNOB_MARGIN;
        int knobY = pillY + KNOB_MARGIN;
        int knobColor = toggled ? KNOB_ON : KNOB_OFF;
        RoundedShaderRenderer.fillGradient(g, knobX, knobY, knobSize, knobSize, knobSize / 2.0F, brighten(knobColor, 16), knobColor);

        String status = toggled ? "ON" : "OFF";
        int statusX = toggled ? pillX + KNOB_MARGIN + 2 : pillX + knobSize + KNOB_MARGIN * 2 + 2;
        int statusY = pillY + (PILL_H - 8) / 2;
        int statusColor = toggled ? 0xA0CCE8FF : 0x70889AAA;
        g.drawString(font, status, statusX, statusY, statusColor, false);
    }

    private static int brighten(int argb, int amount) {
        int a = (argb >> 24) & 0xFF;
        int r = Math.min(255, ((argb >> 16) & 0xFF) + amount);
        int g = Math.min(255, ((argb >> 8) & 0xFF) + amount);
        int b = Math.min(255, (argb & 0xFF) + amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
