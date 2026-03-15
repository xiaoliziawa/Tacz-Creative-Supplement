package com.lirxowo.taczcreativesupplement.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.client.gui.components.refit.IComponentTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PackSelectButton extends Button implements IComponentTooltip {
    private final Component packName;
    private final ItemStack previewItem;
    private final int count;

    public PackSelectButton(int x, int y, Component packName, ItemStack previewItem, int count, OnPress onPress) {
        super(x, y, 18, 18, Component.empty(), onPress, DEFAULT_NARRATION);
        this.packName = packName;
        this.previewItem = previewItem;
        this.count = count;
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        int x = getX(), y = getY();
        if (isHoveredOrFocused()) {
            graphics.blit(GunRefitScreen.SLOT_TEXTURE, x, y, 0, 0, width, height, 18, 18);
        } else {
            graphics.blit(GunRefitScreen.SLOT_TEXTURE, x + 1, y + 1, 1, 1, width - 2, height - 2, 18, 18);
        }

        graphics.renderItem(previewItem, x + 1, y + 1);

        String countText = String.valueOf(count);
        Font font = Minecraft.getInstance().font;
        graphics.pose().pushPose();
        graphics.pose().scale(0.5f, 0.5f, 1.0f);
        int textX = (int) ((x + 17) * 2) - font.width(countText);
        int textY = (int) ((y + 14) * 2);
        graphics.drawString(font, countText, textX, textY, 0xFFFFFF, true);
        graphics.pose().popPose();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    @Override
    public void renderTooltip(Consumer<List<Component>> consumer) {
        if (this.isHoveredOrFocused()) {
            consumer.accept(Collections.singletonList(Component.translatable("gui.taczcreativesupplement.pack_tooltip", packName, count)));
        }
    }
}
