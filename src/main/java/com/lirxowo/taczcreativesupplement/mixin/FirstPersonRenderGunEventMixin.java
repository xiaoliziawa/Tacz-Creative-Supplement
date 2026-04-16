package com.lirxowo.taczcreativesupplement.mixin;

import com.lirxowo.taczcreativesupplement.client.animation.FirstPersonSprintJumpAnimator;
import com.lirxowo.taczcreativesupplement.client.animation.GunWallCollisionAnimator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.client.event.FirstPersonRenderGunEvent;
import com.tacz.guns.client.model.BedrockGunModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonRenderGunEvent.class)
public class FirstPersonRenderGunEventMixin {

    @Inject(method = "applyFirstPersonGunTransform", at = @At("TAIL"), remap = false)
    private static void onApplyFirstPersonGunTransform(LocalPlayer player, ItemStack itemStack, PoseStack poseStack, BedrockGunModel model, float partialTicks, CallbackInfo ci) {
        FirstPersonSprintJumpAnimator.apply(player, poseStack);
        GunWallCollisionAnimator.applyFirstPerson(player, poseStack);
    }
}
