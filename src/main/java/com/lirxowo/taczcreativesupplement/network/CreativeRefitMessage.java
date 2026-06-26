package com.lirxowo.taczcreativesupplement.network;

import com.lirxowo.taczcreativesupplement.Taczcreativesupplement;
import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.ServerMessageRefreshRefitScreen;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CreativeRefitMessage(ResourceLocation attachmentId, int gunSlotIndex,
                                   AttachmentType attachmentType) implements CustomPacketPayload {

    public static final Type<CreativeRefitMessage> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Taczcreativesupplement.MODID, "creative_refit"));

    public static final StreamCodec<ByteBuf, CreativeRefitMessage> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, CreativeRefitMessage::attachmentId,
            ByteBufCodecs.INT, CreativeRefitMessage::gunSlotIndex,
            ByteBufCodecs.idMapper(i -> AttachmentType.values()[i], Enum::ordinal), CreativeRefitMessage::attachmentType,
            CreativeRefitMessage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CreativeRefitMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!TaczSupplementConfig.isPlayerAllowed(player.isCreative())) {
                return;
            }
            Inventory inventory = player.getInventory();
            ItemStack gunItem = inventory.getItem(message.gunSlotIndex);
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                return;
            }
            ItemStack attachmentItem = AttachmentItemBuilder.create().setId(message.attachmentId).build();
            if (iGun.allowAttachment(gunItem, attachmentItem)) {
                iGun.installAttachment(player.registryAccess(), gunItem, attachmentItem);
                AttachmentPropertyManager.postChangeEvent(player, gunItem);
                if (message.attachmentType == AttachmentType.EXTENDED_MAG) {
                    iGun.dropAllAmmo(player, gunItem);
                }
                player.inventoryMenu.broadcastChanges();
                NetworkHandler.sendToClientPlayer(ServerMessageRefreshRefitScreen.INSTANCE, player);
            }
        });
    }
}
