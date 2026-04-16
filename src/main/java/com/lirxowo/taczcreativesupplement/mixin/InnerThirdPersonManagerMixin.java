package com.lirxowo.taczcreativesupplement.mixin;

import com.lirxowo.taczcreativesupplement.client.animation.GunWallCollisionAnimator;
import com.tacz.guns.client.animation.third.InnerThirdPersonManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InnerThirdPersonManager.class)
public class InnerThirdPersonManagerMixin {

    @Inject(method = "setRotationAnglesHead", at = @At("TAIL"), remap = false)
    private static void onSetRotationAnglesHead(LivingEntity entity, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head, float partialTick, CallbackInfo ci) {
        GunWallCollisionAnimator.applyThirdPersonPose(entity, rightArm, leftArm, body, head);
    }
}
