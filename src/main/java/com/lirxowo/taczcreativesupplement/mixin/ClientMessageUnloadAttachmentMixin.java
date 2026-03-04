package com.lirxowo.taczcreativesupplement.mixin;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.ClientMessageUnloadAttachment;
import com.tacz.guns.network.message.ServerMessageRefreshRefitScreen;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientMessageUnloadAttachment.class)
public class ClientMessageUnloadAttachmentMixin {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onHandle(ClientMessageUnloadAttachment message, Supplier<NetworkEvent.Context> contextSupplier, CallbackInfo ci) {
        NetworkEvent.Context context = contextSupplier.get();
        if (!context.getDirection().getReceptionSide().isServer()) {
            return;
        }
        ServerPlayer player = context.getSender();
        if (player == null || !player.isCreative()) {
            return;
        }
        ci.cancel();
        ClientMessageUnloadAttachmentAccessor accessor = (ClientMessageUnloadAttachmentAccessor) (Object) message;
        int gunSlotIndex = accessor.getGunSlotIndex();
        AttachmentType attachmentType = accessor.getAttachmentType();
        context.enqueueWork(() -> {
            Inventory inventory = player.getInventory();
            ItemStack gunItem = inventory.getItem(gunSlotIndex);
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun != null) {
                ItemStack attachmentItem = iGun.getAttachment(gunItem, attachmentType);
                if (!attachmentItem.isEmpty()) {
                    iGun.unloadAttachment(gunItem, attachmentType);
                    AttachmentPropertyManager.postChangeEvent(player, gunItem);
                    if (attachmentType == AttachmentType.EXTENDED_MAG) {
                        iGun.dropAllAmmo(player, gunItem);
                    }
                    player.inventoryMenu.broadcastChanges();
                    NetworkHandler.sendToClientPlayer(new ServerMessageRefreshRefitScreen(), player);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
