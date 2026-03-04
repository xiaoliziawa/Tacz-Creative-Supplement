package com.lirxowo.taczcreativesupplement.mixin;

import com.lirxowo.taczcreativesupplement.client.CreativeAttachmentSlot;
import com.lirxowo.taczcreativesupplement.network.CreativeRefitMessage;
import com.lirxowo.taczcreativesupplement.network.ModNetworkHandler;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.client.animation.screen.RefitTransform;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.client.gui.components.refit.RefitTurnPageButton;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.resource.index.CommonAttachmentIndex;
import com.tacz.guns.sound.SoundManager;
import com.tacz.guns.util.AllowAttachmentTagMatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(GunRefitScreen.class)
public abstract class GunRefitScreenMixin extends Screen {

    @Shadow(remap = false)
    private int currentPage;

    @Unique
    private static final int CREATIVE_SLOT_COUNT = 8;

    protected GunRefitScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "addInventoryAttachmentButtons", at = @At("HEAD"), cancellable = true, remap = false)
    private void onAddInventoryAttachmentButtons(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isCreative()) {
            return;
        }
        ci.cancel();
        taczCreativeSupplement$addCreativeAttachmentButtons();
    }

    @Unique
    private void taczCreativeSupplement$addCreativeAttachmentButtons() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        AttachmentType currentType = RefitTransform.getCurrentTransformType();
        if (currentType == AttachmentType.NONE) {
            return;
        }

        ItemStack gunItem = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return;
        }
        ResourceLocation gunId = iGun.getGunId(gunItem);
        Inventory inventory = player.getInventory();

        List<ResourceLocation> compatibleAttachments = new ArrayList<>();
        Set<Map.Entry<ResourceLocation, CommonAttachmentIndex>> allAttachments = TimelessAPI.getAllCommonAttachmentIndex();
        for (Map.Entry<ResourceLocation, CommonAttachmentIndex> entry : allAttachments) {
            CommonAttachmentIndex attachmentIndex = entry.getValue();
            if (attachmentIndex.getType() != currentType) {
                continue;
            }
            ResourceLocation attachmentId = entry.getKey();
            if (AllowAttachmentTagMatcher.match(gunId, attachmentId)) {
                compatibleAttachments.add(attachmentId);
            }
        }

        int startX = this.width - 30;
        int startY = 50;
        int totalCount = compatibleAttachments.size();
        int totalPage = totalCount > 0 ? (totalCount - 1) / CREATIVE_SLOT_COUNT : 0;
        if (currentPage > totalPage) {
            currentPage = 0;
        }
        int pageStart = currentPage * CREATIVE_SLOT_COUNT;

        for (int i = pageStart; i < Math.min(pageStart + CREATIVE_SLOT_COUNT, totalCount); i++) {
            ResourceLocation attachmentId = compatibleAttachments.get(i);
            ItemStack displayItem = AttachmentItemBuilder.create().setId(attachmentId).build();
            int currentY = startY + (i - pageStart) * GunRefitScreen.SLOT_SIZE;

            CreativeAttachmentSlot slot = new CreativeAttachmentSlot(startX, currentY, displayItem, b -> {
                SoundPlayManager.playerRefitSound(displayItem, player, SoundManager.INSTALL_SOUND);
                CreativeRefitMessage message = new CreativeRefitMessage(attachmentId, inventory.selected, currentType);
                ModNetworkHandler.CHANNEL.sendToServer(message);
            });
            this.addRenderableWidget(slot);
        }

        RefitTurnPageButton turnPageButtonUp = new RefitTurnPageButton(startX, startY - 10, true, b -> {
            if (currentPage > 0) {
                currentPage--;
                this.init(this.minecraft, this.width, this.height);
            }
        });
        RefitTurnPageButton turnPageButtonDown = new RefitTurnPageButton(startX, startY + GunRefitScreen.SLOT_SIZE * CREATIVE_SLOT_COUNT + 2, false, b -> {
            if (currentPage < totalPage) {
                currentPage++;
                this.init(this.minecraft, this.width, this.height);
            }
        });
        if (currentPage < totalPage) {
            this.addRenderableWidget(turnPageButtonDown);
        }
        if (currentPage > 0) {
            this.addRenderableWidget(turnPageButtonUp);
        }
    }
}
