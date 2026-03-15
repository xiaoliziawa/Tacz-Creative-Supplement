package com.lirxowo.taczcreativesupplement.client.screen;

import com.lirxowo.taczcreativesupplement.config.GameModeOption;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ModeCycleButton extends AbstractButton {

    @FunctionalInterface
    public interface OnChange {
        void onChange(ModeCycleButton button, GameModeOption newValue);
    }

    private static final int BADGE_CREATIVE = 0xFF1E5AAA;
    private static final int BADGE_SURVIVAL = 0xFF1E8844;
    private static final int BADGE_BOTH = 0xFF6633AA;

    private static final int CARD_HOVER = 0x28FFFFFF;
    private static final int CARD_NORMAL = 0x12FFFFFF;
    private static final int TEXT_COLOR = 0xFFEEEEFF;

    private GameModeOption value;
    private final OnChange onChange;

    public ModeCycleButton(int x, int y, int w, int h, Component label, OnChange onChange, GameModeOption initial) {
        super(x, y, w, h, label);
        this.onChange = onChange;
        this.value = initial;
    }

    public GameModeOption getValue() {
        return value;
    }

    public void setValue(GameModeOption v) {
        this.value = v;
    }

    @Override
    public void onPress() {
        GameModeOption[] vals = GameModeOption.values();
        value = vals[(value.ordinal() + 1) % vals.length];
        onChange.onChange(this, value);
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partial) {
        RenderSystem.enableBlend();
        Font font = Minecraft.getInstance().font;
        int x = getX(), y = getY(), w = width, h = height;

        g.fill(x, y, x + w, y + h, isHoveredOrFocused() ? CARD_HOVER : CARD_NORMAL);

        int border = isHoveredOrFocused() ? 0x50A0C8FF : 0x20FFFFFF;
        g.fill(x, y, x + w, y + 1, border);
        g.fill(x, y + h - 1, x + w, y + h, border);
        g.fill(x, y, x + 1, y + h, border);
        g.fill(x + w - 1, y, x + w, y + h, border);

        g.drawString(font, getMessage(), x + 12, y + (h - 8) / 2, TEXT_COLOR, false);

        Component valueText = getValueText();
        int textW = font.width(valueText);
        int padH = 8;
        int padV = 4;
        int badgeW = textW + padH * 2;
        int badgeH = 8 + padV * 2;
        int badgeX = x + w - badgeW - 10;
        int badgeY = y + (h - badgeH) / 2;

        int bgColor = getBadgeColor();
        if (isHoveredOrFocused()) bgColor = brighten(bgColor, 0x1A);
        g.fill(badgeX, badgeY, badgeX + badgeW, badgeY + badgeH, bgColor);

        g.fill(badgeX, badgeY, badgeX + badgeW, badgeY + 1, 0x30FFFFFF);
        g.fill(badgeX, badgeY + badgeH - 1, badgeX + badgeW, badgeY + badgeH, 0x20000000);

        g.drawString(font, valueText, badgeX + padH, badgeY + padV, 0xFFEEEEFF, false);

        RenderSystem.disableBlend();
    }

    private int getBadgeColor() {
        switch (value) {
            case SURVIVAL:
                return BADGE_SURVIVAL;
            case BOTH:
                return BADGE_BOTH;
            default:
                return BADGE_CREATIVE;
        }
    }

    private Component getValueText() {
        switch (value) {
            case CREATIVE:
                return Component.translatable("option.taczcreativesupplement.mode.creative");
            case SURVIVAL:
                return Component.translatable("option.taczcreativesupplement.mode.survival");
            case BOTH:
                return Component.translatable("option.taczcreativesupplement.mode.both");
            default:
                return Component.literal("???");
        }
    }

    private static int brighten(int argb, int amount) {
        int a = (argb >> 24) & 0xFF;
        int r = Math.min(255, ((argb >> 16) & 0xFF) + amount);
        int gr = Math.min(255, ((argb >> 8) & 0xFF) + amount);
        int b = Math.min(255, (argb & 0xFF) + amount);
        return (a << 24) | (r << 16) | (gr << 8) | b;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
