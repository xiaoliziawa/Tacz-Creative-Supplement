package com.lirxowo.taczcreativesupplement.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
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

    private static final int PILL_W = 36;
    private static final int PILL_H = 14;

    private static final int KNOB_MARGIN = 2;

    private static final int PILL_ON = 0xFF4A8C3F;
    private static final int PILL_OFF = 0xFF3A3A3A;
    private static final int KNOB_ON = 0xFFD0D0D0;
    private static final int KNOB_OFF = 0xFF666666;
    private static final int CARD_HOVER = 0x30FFFFFF;
    private static final int CARD_NORMAL = 0x18FFFFFF;
    private static final int TEXT_ACTIVE = 0xFFD0D0D0;
    private static final int TEXT_INACTIVE = 0xFF707070;

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
        RenderSystem.enableBlend();

        Font font = Minecraft.getInstance().font;
        int x = getX(), y = getY(), w = width, h = height;
        int cardBg = isHoveredOrFocused() ? CARD_HOVER : CARD_NORMAL;
        g.fill(x, y, x + w, y + h, cardBg);
        int border = isHoveredOrFocused() ? 0x50AAAAAA : 0x20FFFFFF;
        g.fill(x, y, x + w, y + 1, border);
        g.fill(x, y + h - 1, x + w, y + h, border);
        g.fill(x, y, x + 1, y + h, border);
        g.fill(x + w - 1, y, x + w, y + h, border);
        int labelColor = isActive() ? TEXT_ACTIVE : TEXT_INACTIVE;
        int labelY = y + (h - 8) / 2;
        g.drawString(font, getMessage(), x + 8, labelY, labelColor, false);
        int pillX = x + w - PILL_W - 8;
        int pillY = y + (h - PILL_H) / 2;
        int pillColor = toggled ? PILL_ON : PILL_OFF;
        g.fill(pillX, pillY, pillX + PILL_W, pillY + PILL_H, pillColor);
        int knobSize = PILL_H - KNOB_MARGIN * 2;
        int knobX = toggled ? pillX + PILL_W - knobSize - KNOB_MARGIN : pillX + KNOB_MARGIN;
        int knobY = pillY + KNOB_MARGIN;
        int knobColor = toggled ? KNOB_ON : KNOB_OFF;
        g.fill(knobX, knobY, knobX + knobSize, knobY + knobSize, knobColor);

        RenderSystem.disableBlend();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
