package com.lirxowo.taczcreativesupplement.network;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.ServerMessageRefreshRefitScreen;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CreativeRefitMessage {
    private final ResourceLocation attachmentId;
    private final int gunSlotIndex;
    private final AttachmentType attachmentType;

    public CreativeRefitMessage(ResourceLocation attachmentId, int gunSlotIndex, AttachmentType attachmentType) {
        this.attachmentId = attachmentId;
        this.gunSlotIndex = gunSlotIndex;
        this.attachmentType = attachmentType;
    }

    public static void encode(CreativeRefitMessage message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.attachmentId);
        buf.writeInt(message.gunSlotIndex);
        buf.writeEnum(message.attachmentType);
    }

    public static CreativeRefitMessage decode(FriendlyByteBuf buf) {
        return new CreativeRefitMessage(buf.readResourceLocation(), buf.readInt(), buf.readEnum(AttachmentType.class));
    }

    public static void handle(CreativeRefitMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null) {
                    return;
                }
                if (!player.isCreative()) {
                    return;
                }
                Inventory inventory = player.getInventory();
                ItemStack gunItem = inventory.getItem(message.gunSlotIndex);
                IGun iGun = IGun.getIGunOrNull(gunItem);
                if (iGun == null) {
                    return;
                }
                ItemStack attachmentItem = AttachmentItemBuilder.create()
                        .setId(message.attachmentId)
                        .build();
                if (iGun.allowAttachment(gunItem, attachmentItem)) {
                    ItemStack oldAttachment = iGun.getAttachment(gunItem, message.attachmentType);
                    iGun.installAttachment(gunItem, attachmentItem);
                    AttachmentPropertyManager.postChangeEvent(player, gunItem);
                    if (message.attachmentType == AttachmentType.EXTENDED_MAG) {
                        iGun.dropAllAmmo(player, gunItem);
                    }
                    player.inventoryMenu.broadcastChanges();
                    NetworkHandler.sendToClientPlayer(new ServerMessageRefreshRefitScreen(), player);
                }
            });
        }
        context.setPacketHandled(true);
    }
}
