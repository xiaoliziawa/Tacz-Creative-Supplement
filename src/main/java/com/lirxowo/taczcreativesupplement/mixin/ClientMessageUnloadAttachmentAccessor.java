package com.lirxowo.taczcreativesupplement.mixin;

import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.network.message.ClientMessageUnloadAttachment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientMessageUnloadAttachment.class)
public interface ClientMessageUnloadAttachmentAccessor {
    @Accessor(value = "gunSlotIndex", remap = false)
    int getGunSlotIndex();

    @Accessor(value = "attachmentType", remap = false)
    AttachmentType getAttachmentType();
}
