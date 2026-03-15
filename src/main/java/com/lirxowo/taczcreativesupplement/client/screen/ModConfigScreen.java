package com.lirxowo.taczcreativesupplement.client.screen;

import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigScreen extends Screen {

    private static final int PANEL_W = 390;
    private static final int PANEL_H = 270;
    private static final int TITLE_H = 44;

    private static final int BG_OUTER = 0xF00B1220;
    private static final int BG_INNER_TL = 0xEE111D30;
    private static final int BG_INNER_BR = 0xEE0C1524;
    private static final int TITLE_BAR_TOP = 0xFF08101E;
    private static final int TITLE_BAR_BOT = 0xFF0D1828;
    private static final int EDGE_TOP = 0x55A8CCFF;
    private static final int EDGE_SIDE = 0x20A8CCFF;
    private static final int EDGE_BOTTOM = 0x15000000;
    private static final int ACCENT_DIVIDER = 0x7033A8FF;
    private static final int ACCENT_DOT = 0xFF3F8FFF;
    private static final int ACCENT_DOT2 = 0xFF2E6FDD;
    private static final int TEXT_TITLE = 0xFFE8F0FF;
    private static final int TEXT_SUBTITLE = 0xFF7A90B0;
    private static final int TEXT_LABEL = 0xFFCCDDEE;
    private static final int GLOW_TOP = 0x1A8BB8FF;
    private static final int BOTTOM_STRIP = 0x220A1020;

    private final Screen previous;
    private int px, py;
    private boolean enabled;

    public ModConfigScreen(Screen previous) {
        super(Component.translatable("screen.taczcreativesupplement.config.title"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        px = (width - PANEL_W) / 2;
        py = (height - PANEL_H) / 2;
        enabled = TaczSupplementConfig.ENABLE_CREATIVE_SUPPLEMENT.get();

        int contentTop = py + TITLE_H;
        int btnW = 280, btnH = 32;
        int btnX = px + (PANEL_W - btnW) / 2;

        addRenderableWidget(new ToggleButton(btnX, contentTop + 30, btnW, btnH, Component.translatable("option.taczcreativesupplement.enable"), btn -> {
            enabled = !enabled;
            TaczSupplementConfig.ENABLE_CREATIVE_SUPPLEMENT.set(enabled);
            btn.setToggled(enabled);
        }, enabled));

        addRenderableWidget(new ModeCycleButton(btnX, contentTop + 70, btnW, btnH, Component.translatable("option.taczcreativesupplement.mode"), (btn, newVal) -> TaczSupplementConfig.GAME_MODE.set(newVal), TaczSupplementConfig.GAME_MODE.get()));

        int doneW = 120, doneH = 22;
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), btn -> onClose()).bounds(px + (PANEL_W - doneW) / 2, py + PANEL_H - 34, doneW, doneH).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float delta) {
        renderBackground(g);
        renderPanel(g);
        renderTitleBar(g);
        renderContent(g);
        super.render(g, mx, my, delta);
    }

    private void renderPanel(GuiGraphics g) {
        RenderSystem.enableBlend();
        int x = px, y = py, w = PANEL_W, h = PANEL_H;

        g.fill(x, y, x + w, y + h, BG_OUTER);
        g.fillGradient(x + 1, y + TITLE_H, x + w / 2, y + h - 1, BG_INNER_TL, BG_INNER_BR);
        g.fillGradient(x + w / 2, y + TITLE_H, x + w - 1, y + h - 1, BG_INNER_BR, BG_INNER_BR);
        g.fillGradient(x + 1, y + 1, x + w - 1, y + TITLE_H, TITLE_BAR_TOP, TITLE_BAR_BOT);
        g.fillGradient(x + 2, y + TITLE_H, x + w - 2, y + TITLE_H + 10, GLOW_TOP, 0x00000000);
        g.fill(x + 2, y + TITLE_H - 1, x + w - 2, y + TITLE_H, ACCENT_DIVIDER);
        g.fillGradient(x + 1, y + h - 48, x + w - 1, y + h - 1, 0x00000000, BOTTOM_STRIP);

        g.fill(x, y, x + w, y + 1, EDGE_TOP);
        g.fill(x, y, x + 1, y + h, EDGE_SIDE);
        g.fill(x + w - 1, y, x + w, y + h, EDGE_SIDE);
        g.fill(x, y + h - 1, x + w, y + h, EDGE_BOTTOM);

        g.fill(x, y, x + 1, y + 1, 0x00000000);
        g.fill(x + w - 1, y, x + w, y + 1, 0x00000000);
        g.fill(x, y + h - 1, x + 1, y + h, 0x00000000);
        g.fill(x + w - 1, y + h - 1, x + w, y + h, 0x00000000);

        g.fill(x + 1, y + 1, x + w - 1, y + 2, 0x18FFFFFF);

        int dotY = y + (TITLE_H / 2) - 2;
        g.fill(x + 12, dotY, x + 16, dotY + 4, ACCENT_DOT);
        g.fill(x + 20, dotY, x + 24, dotY + 4, ACCENT_DOT2);
        g.fill(x + 28, dotY, x + 32, dotY + 4, ACCENT_DOT);

        RenderSystem.disableBlend();
    }

    private void renderTitleBar(GuiGraphics g) {
        int cx = px + PANEL_W / 2;
        g.drawCenteredString(font, this.title, cx, py + 10, TEXT_TITLE);
        g.drawCenteredString(font, Component.translatable("screen.taczcreativesupplement.config.subtitle"), cx, py + 24, TEXT_SUBTITLE);
    }

    private void renderContent(GuiGraphics g) {
        int cx = px + PANEL_W / 2;
        int top = py + TITLE_H;

        g.drawCenteredString(font, Component.translatable("screen.taczcreativesupplement.config.section"), cx, top + 14, TEXT_LABEL);

        boolean on = TaczSupplementConfig.ENABLE_CREATIVE_SUPPLEMENT.get();
        Component status = on ? Component.translatable("screen.taczcreativesupplement.status.enabled").withStyle(ChatFormatting.GREEN) : Component.translatable("screen.taczcreativesupplement.status.disabled").withStyle(ChatFormatting.RED);
        g.drawCenteredString(font, status, cx, top + 114, 0xFFFFFFFF);

        g.drawCenteredString(font, Component.translatable("screen.taczcreativesupplement.hint"), cx, top + 130, 0xFF3A4A60);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(previous);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
