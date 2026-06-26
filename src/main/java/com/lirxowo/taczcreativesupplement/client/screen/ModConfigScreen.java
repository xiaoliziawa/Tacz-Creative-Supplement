package com.lirxowo.taczcreativesupplement.client.screen;

import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigScreen extends Screen {

    private static final int TITLE_H = 36;
    private static final int BG_COLOR = 0xE0242424;
    private static final int BORDER_COLOR = 0xFF555555;
    private static final int TITLE_BG = 0xFF1A1A1A;
    private static final int DIVIDER_COLOR = 0xFF484848;
    private static final int TEXT_TITLE = 0xFFE0E0E0;
    private static final int TEXT_SUBTITLE = 0xFF888888;
    private static final int TEXT_LABEL = 0xFFAAAAAA;

    private final Screen previous;
    private int panelW, panelH;
    private int px, py;
    private boolean enabled;

    public ModConfigScreen(Screen previous) {
        super(Component.translatable("screen.taczcreativesupplement.config.title"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        panelW = Math.min(310, width - 20);
        panelH = Math.min(210, height - 20);
        px = (width - panelW) / 2;
        py = (height - panelH) / 2;
        enabled = TaczSupplementConfig.ENABLE_CREATIVE_SUPPLEMENT.get();

        int contentTop = py + TITLE_H;
        int btnW = Math.min(230, panelW - 30);
        int btnH = 24;
        int btnX = px + (panelW - btnW) / 2;

        addRenderableWidget(new ToggleButton(btnX, contentTop + 22, btnW, btnH, Component.translatable("option.taczcreativesupplement.enable"), btn -> {
            enabled = !enabled;
            TaczSupplementConfig.ENABLE_CREATIVE_SUPPLEMENT.set(enabled);
            btn.setToggled(enabled);
        }, enabled));

        addRenderableWidget(new ModeCycleButton(btnX, contentTop + 52, btnW, btnH, Component.translatable("option.taczcreativesupplement.mode"), (btn, newVal) -> TaczSupplementConfig.GAME_MODE.set(newVal), TaczSupplementConfig.GAME_MODE.get()));

        int doneW = 100, doneH = 20;
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), btn -> onClose()).bounds(px + (panelW - doneW) / 2, py + panelH - 28, doneW, doneH).build());
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
        int x = px, y = py, w = panelW, h = panelH;

        g.fill(x, y, x + w, y + h, BG_COLOR);
        g.fill(x, y, x + w, y + TITLE_H, TITLE_BG);
        g.fill(x, y + TITLE_H, x + w, y + TITLE_H + 1, DIVIDER_COLOR);

        g.fill(x, y, x + w, y + 1, BORDER_COLOR);
        g.fill(x, y + h - 1, x + w, y + h, BORDER_COLOR);
        g.fill(x, y, x + 1, y + h, BORDER_COLOR);
        g.fill(x + w - 1, y, x + w, y + h, BORDER_COLOR);

        RenderSystem.disableBlend();
    }

    private void renderTitleBar(GuiGraphics g) {
        int cx = px + panelW / 2;
        g.drawCenteredString(font, this.title, cx, py + 8, TEXT_TITLE);
        g.drawCenteredString(font, Component.translatable("screen.taczcreativesupplement.config.subtitle"), cx, py + 22, TEXT_SUBTITLE);
    }

    private void renderContent(GuiGraphics g) {
        int cx = px + panelW / 2;
        int top = py + TITLE_H;

        g.drawCenteredString(font, Component.translatable("screen.taczcreativesupplement.config.section"), cx, top + 10, TEXT_LABEL);

        boolean on = TaczSupplementConfig.ENABLE_CREATIVE_SUPPLEMENT.get();
        Component status = on ? Component.translatable("screen.taczcreativesupplement.status.enabled").withStyle(ChatFormatting.GREEN) : Component.translatable("screen.taczcreativesupplement.status.disabled").withStyle(ChatFormatting.RED);
        g.drawCenteredString(font, status, cx, top + 86, 0xFFFFFFFF);

        g.drawCenteredString(font, Component.translatable("screen.taczcreativesupplement.hint"), cx, top + 100, TEXT_SUBTITLE);
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
