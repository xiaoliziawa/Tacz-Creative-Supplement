package com.lirxowo.taczcreativesupplement.client.screen;

import com.lirxowo.taczcreativesupplement.client.render.RoundedShaderRenderer;
import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigScreen extends Screen {

    private static final int DEFAULT_PANEL_W = 360;
    private static final int DEFAULT_PANEL_H = 260;
    private static final int TITLE_H = 40;
    private static final float PANEL_RADIUS = 18.0F;
    private static final int PANEL_MARGIN = 28;

    private static final int BG_OUTER = 0xCC07111D;
    private static final int BG_INNER_TL = 0xEE13233A;
    private static final int BG_INNER_BR = 0xEE0B1525;
    private static final int TITLE_BAR_TOP = 0xF61B3558;
    private static final int TITLE_BAR_BOT = 0xE6101E33;
    private static final int EDGE = 0x885CB6FF;
    private static final int ACCENT_DIVIDER = 0x7A40A8FF;
    private static final int ACCENT_DOT = 0xFF3F8FFF;
    private static final int ACCENT_DOT2 = 0xFF2E6FDD;
    private static final int TEXT_TITLE = 0xFFE8F0FF;
    private static final int TEXT_SUBTITLE = 0xFF7A90B0;
    private static final int TEXT_LABEL = 0xFFCCDDEE;
    private static final int GLOW_TOP = 0x1A8BB8FF;
    private static final int STATUS_BG = 0x2A15263F;
    private static final int HINT_BG = 0x20111A2A;

    private final Screen previous;
    private int px;
    private int py;
    private int panelWidth;
    private int panelHeight;
    private int buttonWidth;
    private int buttonHeight;
    private int toggleY;
    private int animationY;
    private int modeY;
    private int hintY;
    private int statusY;
    private boolean enabled;
    private boolean sprintJumpAnimationEnabled;

    public ModConfigScreen(Screen previous) {
        super(Component.translatable("screen.taczcreativesupplement.config.title"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        panelWidth = Math.min(DEFAULT_PANEL_W, Math.max(300, width - PANEL_MARGIN * 2));
        panelHeight = Math.min(DEFAULT_PANEL_H, Math.max(228, height - PANEL_MARGIN * 2));
        panelWidth = Math.min(panelWidth, width - 12);
        panelHeight = Math.min(panelHeight, height - 12);
        px = (width - panelWidth) / 2;
        py = (height - panelHeight) / 2;
        enabled = TaczSupplementConfig.ENABLE_CREATIVE_SUPPLEMENT.get();
        sprintJumpAnimationEnabled = TaczSupplementConfig.ENABLE_SPRINT_JUMP_ANIMATION.get();

        int contentTop = py + TITLE_H;
        buttonWidth = Math.min(280, panelWidth - 48);
        buttonHeight = panelHeight <= 240 ? 28 : 30;
        int buttonX = px + (panelWidth - buttonWidth) / 2;
        toggleY = contentTop + 24;
        animationY = toggleY + buttonHeight + 10;
        modeY = animationY + buttonHeight + 10;

        int doneWidth = Math.min(128, panelWidth - 96);
        int doneHeight = 22;
        int doneY = py + panelHeight - doneHeight - 12;
        statusY = doneY - 18;
        hintY = Math.min(modeY + buttonHeight + 12, statusY - 16);

        addRenderableWidget(new ToggleButton(buttonX, toggleY, buttonWidth, buttonHeight, Component.translatable("option.taczcreativesupplement.enable"), btn -> {
            enabled = !enabled;
            TaczSupplementConfig.ENABLE_CREATIVE_SUPPLEMENT.set(enabled);
            btn.setToggled(enabled);
        }, enabled));

        addRenderableWidget(new ToggleButton(buttonX, animationY, buttonWidth, buttonHeight, Component.translatable("option.taczcreativesupplement.sprint_jump_animation"), btn -> {
            sprintJumpAnimationEnabled = !sprintJumpAnimationEnabled;
            TaczSupplementConfig.ENABLE_SPRINT_JUMP_ANIMATION.set(sprintJumpAnimationEnabled);
            btn.setToggled(sprintJumpAnimationEnabled);
        }, sprintJumpAnimationEnabled));

        addRenderableWidget(new ModeCycleButton(buttonX, modeY, buttonWidth, buttonHeight, Component.translatable("option.taczcreativesupplement.mode"), (btn, newVal) -> TaczSupplementConfig.GAME_MODE.set(newVal), TaczSupplementConfig.GAME_MODE.get()));

        addRenderableWidget(new RoundedTextButton(px + (panelWidth - doneWidth) / 2, doneY, doneWidth, doneHeight, Component.translatable("gui.done"), btn -> onClose()));
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
        int x = px;
        int y = py;
        int w = panelWidth;
        int h = panelHeight;
        int contentTop = py + TITLE_H;
        int optionCardY = contentTop + 8;
        int optionCardHeight = modeY + buttonHeight - optionCardY + 12;

        RoundedShaderRenderer.frame(g, x, y, w, h, PANEL_RADIUS, 2.0F, EDGE, BG_OUTER, BG_OUTER);
        RoundedShaderRenderer.fillGradient(g, x + 3.0F, y + 3.0F, w - 6.0F, h - 6.0F, PANEL_RADIUS - 3.0F, BG_INNER_TL, BG_INNER_BR);
        RoundedShaderRenderer.fillGradient(g, x + 8.0F, y + 8.0F, w - 16.0F, TITLE_H, PANEL_RADIUS - 7.0F, TITLE_BAR_TOP, TITLE_BAR_BOT);
        RoundedShaderRenderer.fillGradient(g, x + 14.0F, optionCardY, w - 28.0F, optionCardHeight, 14.0F, 0x181B304E, 0x12111B2D);
        RoundedShaderRenderer.fillGradient(g, x + 24.0F, hintY - 4.0F, w - 48.0F, 16.0F, 8.0F, HINT_BG, HINT_BG);
        RoundedShaderRenderer.fillGradient(g, x + 18.0F, statusY - 5.0F, w - 36.0F, 18.0F, 10.0F, STATUS_BG, STATUS_BG);
        RoundedShaderRenderer.fillGradient(g, x + 14.0F, y + TITLE_H - 2.0F, w - 28.0F, 8.0F, 4.0F, GLOW_TOP, 0x00000000);
        RoundedShaderRenderer.fillGradient(g, x + 16.0F, y + TITLE_H - 1.0F, w - 32.0F, 2.0F, 1.0F, ACCENT_DIVIDER, ACCENT_DIVIDER);

        int dotY = y + (TITLE_H / 2) - 2;
        RoundedShaderRenderer.fill(g, x + 12.0F, dotY, 4.0F, 4.0F, 2.0F, ACCENT_DOT);
        RoundedShaderRenderer.fill(g, x + 20.0F, dotY, 4.0F, 4.0F, 2.0F, ACCENT_DOT2);
        RoundedShaderRenderer.fill(g, x + 28.0F, dotY, 4.0F, 4.0F, 2.0F, ACCENT_DOT);
    }

    private void renderTitleBar(GuiGraphics g) {
        int centerX = px + panelWidth / 2;
        g.drawCenteredString(font, this.title, centerX, py + 10, TEXT_TITLE);
        g.drawCenteredString(font, Component.translatable("screen.taczcreativesupplement.config.subtitle"), centerX, py + 22, TEXT_SUBTITLE);
    }

    private void renderContent(GuiGraphics g) {
        int centerX = px + panelWidth / 2;
        int top = py + TITLE_H;

        g.drawCenteredString(font, Component.translatable("screen.taczcreativesupplement.config.section"), centerX, top + 14, TEXT_LABEL);
        g.drawCenteredString(font, Component.translatable("screen.taczcreativesupplement.hint"), centerX, hintY, 0xFF8091AC);

        boolean on = TaczSupplementConfig.ENABLE_CREATIVE_SUPPLEMENT.get();
        Component status = on
                ? Component.translatable("screen.taczcreativesupplement.status.enabled").withStyle(ChatFormatting.GREEN)
                : Component.translatable("screen.taczcreativesupplement.status.disabled").withStyle(ChatFormatting.RED);
        g.drawCenteredString(font, status, centerX, statusY, 0xFFFFFFFF);
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
