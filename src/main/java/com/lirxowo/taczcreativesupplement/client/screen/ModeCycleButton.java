package com.lirxowo.taczcreativesupplement.client.screen;

import com.lirxowo.taczcreativesupplement.client.render.RoundedShaderRenderer;
import com.lirxowo.taczcreativesupplement.config.GameModeOption;
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
    private static final int BADGE_ADVENTURE = 0xFFB36A1D;
    private static final int BADGE_ADVENTURE_CREATIVE = 0xFF0D8D8A;

    private static final int CARD_OUTLINE = 0x585DB5FF;
    private static final int CARD_OUTLINE_HOVER = 0x8A7FD0FF;
    private static final int CARD_TOP = 0x26161F2E;
    private static final int CARD_BOTTOM = 0x20101928;
    private static final int CARD_TOP_HOVER = 0x32213146;
    private static final int CARD_BOTTOM_HOVER = 0x26152034;
    private static final int TEXT_COLOR = 0xFFEEEEFF;

    private GameModeOption value;
    private final OnChange onChange;

    public ModeCycleButton(int x, int y, int width, int height, Component label, OnChange onChange, GameModeOption initial) {
        super(x, y, width, height, label);
        this.onChange = onChange;
        this.value = initial;
    }

    public GameModeOption getValue() {
        return value;
    }

    public void setValue(GameModeOption value) {
        this.value = value;
    }

    @Override
    public void onPress() {
        GameModeOption[] values = GameModeOption.values();
        value = values[(value.ordinal() + 1) % values.length];
        onChange.onChange(this, value);
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

        g.drawString(font, getMessage(), x + 12, y + (h - 8) / 2, TEXT_COLOR, false);

        Component valueText = getValueText();
        int textWidth = font.width(valueText);
        int padH = 8;
        int padV = 4;
        int badgeWidth = textWidth + padH * 2;
        int badgeHeight = 8 + padV * 2;
        int badgeX = x + w - badgeWidth - 10;
        int badgeY = y + (h - badgeHeight) / 2;

        int badgeColor = getBadgeColor();
        if (isHoveredOrFocused()) {
            badgeColor = brighten(badgeColor, 0x1A);
        }
        RoundedShaderRenderer.frame(g, badgeX, badgeY, badgeWidth, badgeHeight, 9.0F, 1.0F, 0x38FFFFFF, brighten(badgeColor, 14), badgeColor);
        g.drawString(font, valueText, badgeX + padH, badgeY + padV, 0xFFEEEEFF, false);
    }

    private int getBadgeColor() {
        switch (value) {
            case SURVIVAL:
                return BADGE_SURVIVAL;
            case BOTH:
                return BADGE_BOTH;
            case ADVENTURE:
                return BADGE_ADVENTURE;
            case ADVENTURE_CREATIVE:
                return BADGE_ADVENTURE_CREATIVE;
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
            case ADVENTURE:
                return Component.translatable("option.taczcreativesupplement.mode.adventure");
            case ADVENTURE_CREATIVE:
                return Component.translatable("option.taczcreativesupplement.mode.adventure_creative");
            default:
                return Component.literal("???");
        }
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
