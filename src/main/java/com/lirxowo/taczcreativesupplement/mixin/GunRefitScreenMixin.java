package com.lirxowo.taczcreativesupplement.mixin;

import com.lirxowo.taczcreativesupplement.client.CreativeAttachmentSlot;
import com.lirxowo.taczcreativesupplement.client.data.AttachmentOrganizer;
import com.lirxowo.taczcreativesupplement.client.data.AttachmentOrganizer.PackGroup;
import com.lirxowo.taczcreativesupplement.client.screen.BackButton;
import com.lirxowo.taczcreativesupplement.client.screen.PackSelectButton;
import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.lirxowo.taczcreativesupplement.network.CreativeRefitMessage;
import com.lirxowo.taczcreativesupplement.network.ModNetworkHandler;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.client.animation.screen.RefitTransform;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.client.gui.components.refit.RefitTurnPageButton;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.sound.SoundManager;
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

import java.util.List;

@Mixin(GunRefitScreen.class)
public abstract class GunRefitScreenMixin extends Screen {

    @Shadow(remap = false)
    private int currentPage;

    @Unique
    private static final int CREATIVE_SLOT_COUNT = 8;

    @Unique
    private String taczCS$selectedNamespace = null;

    @Unique
    private int taczCS$packPage = 0;

    @Unique
    private AttachmentType taczCS$lastType = null;

    protected GunRefitScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "addInventoryAttachmentButtons", at = @At("HEAD"), cancellable = true, remap = false)
    private void onAddInventoryAttachmentButtons(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        if (!TaczSupplementConfig.isPlayerAllowed(mc.player.isCreative())) {
            return;
        }
        ci.cancel();
        taczCS$addCreativeAttachmentButtons();
    }

    @Unique
    private void taczCS$addCreativeAttachmentButtons() {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        AttachmentType currentType = RefitTransform.getCurrentTransformType();
        if (currentType == AttachmentType.NONE) return;

        if (currentType != taczCS$lastType) {
            taczCS$lastType = currentType;
            taczCS$selectedNamespace = null;
            taczCS$packPage = 0;
            currentPage = 0;
        }

        ItemStack gunItem = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) return;
        ResourceLocation gunId = iGun.getGunId(gunItem);

        List<PackGroup> groups = AttachmentOrganizer.organize(gunId, currentType);
        if (groups.isEmpty()) return;

        if (groups.size() == 1) {
            taczCS$showAttachmentList(groups.get(0), false);
        } else if (taczCS$selectedNamespace != null) {
            PackGroup selected = null;
            for (PackGroup g : groups) {
                if (g.namespace().equals(taczCS$selectedNamespace)) {
                    selected = g;
                    break;
                }
            }
            if (selected != null) {
                taczCS$showAttachmentList(selected, true);
            } else {
                taczCS$selectedNamespace = null;
                taczCS$showPackList(groups);
            }
        } else {
            taczCS$showPackList(groups);
        }
    }

    @Unique
    private void taczCS$showPackList(List<PackGroup> groups) {
        int startX = this.width - 30;
        int startY = 50;
        int totalCount = groups.size();
        int totalPage = totalCount > 0 ? (totalCount - 1) / CREATIVE_SLOT_COUNT : 0;
        if (taczCS$packPage > totalPage) {
            taczCS$packPage = 0;
        }
        int pageStart = taczCS$packPage * CREATIVE_SLOT_COUNT;

        for (int i = pageStart; i < Math.min(pageStart + CREATIVE_SLOT_COUNT, totalCount); i++) {
            PackGroup pack = groups.get(i);
            int currentY = startY + (i - pageStart) * GunRefitScreen.SLOT_SIZE;

            ItemStack previewItem = ItemStack.EMPTY;
            if (!pack.attachmentIds().isEmpty()) {
                previewItem = AttachmentItemBuilder.create().setId(pack.attachmentIds().get(0)).build();
            }

            PackSelectButton button = new PackSelectButton(startX, currentY, pack.displayName(), previewItem, pack.attachmentIds().size(), b -> {
                taczCS$selectedNamespace = pack.namespace();
                currentPage = 0;
                this.init(this.minecraft, this.width, this.height);
            });
            this.addRenderableWidget(button);
        }

        RefitTurnPageButton turnPageUp = new RefitTurnPageButton(startX, startY - 10, true, b -> {
            if (taczCS$packPage > 0) {
                taczCS$packPage--;
                this.init(this.minecraft, this.width, this.height);
            }
        });
        RefitTurnPageButton turnPageDown = new RefitTurnPageButton(startX, startY + GunRefitScreen.SLOT_SIZE * CREATIVE_SLOT_COUNT + 2, false, b -> {
            if (taczCS$packPage < totalPage) {
                taczCS$packPage++;
                this.init(this.minecraft, this.width, this.height);
            }
        });
        if (taczCS$packPage < totalPage) {
            this.addRenderableWidget(turnPageDown);
        }
        if (taczCS$packPage > 0) {
            this.addRenderableWidget(turnPageUp);
        }
    }

    @Unique
    private void taczCS$showAttachmentList(PackGroup group, boolean showBack) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        Inventory inventory = player.getInventory();

        int startX = this.width - 30;
        int startY = 50;
        int slotsPerPage = showBack ? (CREATIVE_SLOT_COUNT - 1) : CREATIVE_SLOT_COUNT;
        int totalAttachments = group.attachmentIds().size();
        int totalPage = totalAttachments > 0 ? (totalAttachments - 1) / slotsPerPage : 0;
        if (currentPage > totalPage) {
            currentPage = 0;
        }

        int slotIndex = 0;

        if (showBack) {
            BackButton backButton = new BackButton(startX, startY, b -> {
                taczCS$selectedNamespace = null;
                currentPage = 0;
                this.init(this.minecraft, this.width, this.height);
            });
            this.addRenderableWidget(backButton);
            slotIndex = 1;
        }

        int pageStart = currentPage * slotsPerPage;
        AttachmentType currentType = RefitTransform.getCurrentTransformType();
        for (int i = pageStart; i < Math.min(pageStart + slotsPerPage, totalAttachments); i++) {
            ResourceLocation attachmentId = group.attachmentIds().get(i);
            ItemStack displayItem = AttachmentItemBuilder.create().setId(attachmentId).build();
            int currentY = startY + slotIndex * GunRefitScreen.SLOT_SIZE;

            CreativeAttachmentSlot slot = new CreativeAttachmentSlot(startX, currentY, displayItem, b -> {
                SoundPlayManager.playerRefitSound(displayItem, player, SoundManager.INSTALL_SOUND);
                CreativeRefitMessage message = new CreativeRefitMessage(attachmentId, inventory.selected, currentType);
                ModNetworkHandler.CHANNEL.sendToServer(message);
            });
            this.addRenderableWidget(slot);
            slotIndex++;
        }

        RefitTurnPageButton turnPageUp = new RefitTurnPageButton(startX, startY - 10, true, b -> {
            if (currentPage > 0) {
                currentPage--;
                this.init(this.minecraft, this.width, this.height);
            }
        });
        RefitTurnPageButton turnPageDown = new RefitTurnPageButton(startX, startY + GunRefitScreen.SLOT_SIZE * CREATIVE_SLOT_COUNT + 2, false, b -> {
            if (currentPage < totalPage) {
                currentPage++;
                this.init(this.minecraft, this.width, this.height);
            }
        });
        if (currentPage < totalPage) {
            this.addRenderableWidget(turnPageDown);
        }
        if (currentPage > 0) {
            this.addRenderableWidget(turnPageUp);
        }
    }
}
