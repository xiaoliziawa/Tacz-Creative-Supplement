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

    private static final int PILL_W = 52;
    private static final int PILL_H = 22;

    private static final int KNOB_MARGIN = 3;

    private static final int PILL_ON_TOP = 0xFF2D6ECC;
    private static final int PILL_ON_BOT = 0xFF1A4FA0;
    private static final int PILL_OFF_TOP = 0xFF2B2B3A;
    private static final int PILL_OFF_BOT = 0xFF1E1E2C;
    private static final int KNOB_ON = 0xFFDDEEFF;
    private static final int KNOB_OFF = 0xFF7788AA;
    private static final int CARD_HOVER = 0x28FFFFFF;
    private static final int CARD_NORMAL = 0x12FFFFFF;
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
        RenderSystem.enableBlend();

        Font font = Minecraft.getInstance().font;
        int x = getX(), y = getY(), w = width, h = height;
        int cardBg = isHoveredOrFocused() ? CARD_HOVER : CARD_NORMAL;
        g.fill(x, y, x + w, y + h, cardBg);
        int border = isHoveredOrFocused() ? 0x50A0C8FF : 0x20FFFFFF;
        g.fill(x, y, x + w, y + 1, border);   // top
        g.fill(x, y + h - 1, x + w, y + h, border);   // bottom
        g.fill(x, y, x + 1, y + h, border);   // left
        g.fill(x + w - 1, y, x + w, y + h, border);   // right
        int labelColor = isActive() ? TEXT_ACTIVE : TEXT_INACTIVE;
        int labelY = y + (h - 8) / 2;
        g.drawString(font, getMessage(), x + 12, labelY, labelColor, false);
        int pillX = x + w - PILL_W - 10;
        int pillY = y + (h - PILL_H) / 2;
        int pillTop = toggled ? PILL_ON_TOP : PILL_OFF_TOP;
        int pillBot = toggled ? PILL_ON_BOT : PILL_OFF_BOT;
        g.fillGradient(pillX, pillY, pillX + PILL_W, pillY + PILL_H / 2, pillTop, pillTop);
        g.fillGradient(pillX, pillY + PILL_H / 2, pillX + PILL_W, pillY + PILL_H, pillTop, pillBot);
        g.fill(pillX, pillY, pillX + PILL_W, pillY + 1, 0x25FFFFFF);
        g.fill(pillX, pillY + PILL_H - 1, pillX + PILL_W, pillY + PILL_H, 0x20000000);
        g.fill(pillX, pillY, pillX + 1, pillY + PILL_H, 0x15FFFFFF);
        g.fill(pillX + PILL_W - 1, pillY, pillX + PILL_W, pillY + PILL_H, 0x10000000);
        int knobSize = PILL_H - KNOB_MARGIN * 2;
        int knobX = toggled ? pillX + PILL_W - knobSize - KNOB_MARGIN : pillX + KNOB_MARGIN;
        int knobY = pillY + KNOB_MARGIN;

        g.fill(knobX + 1, knobY + 1, knobX + knobSize + 1, knobY + knobSize + 1, 0x40000000);
        int knobColor = toggled ? KNOB_ON : KNOB_OFF;
        g.fill(knobX, knobY, knobX + knobSize, knobY + knobSize, knobColor);
        g.fill(knobX, knobY, knobX + knobSize, knobY + 1, 0x40FFFFFF);
        g.fill(knobX, knobY, knobX + 1, knobY + knobSize, 0x25FFFFFF);

        String status = toggled ? "ON" : "OFF";
        int statusX = toggled ? pillX + KNOB_MARGIN + 2 : pillX + knobSize + KNOB_MARGIN * 2 + 2;
        int statusY = pillY + (PILL_H - 8) / 2;
        int statusColor = toggled ? 0xA0CCE8FF : 0x70889AAA;
        g.drawString(font, status, statusX, statusY, statusColor, false);

        RenderSystem.disableBlend();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
