package com.lirxowo.taczcreativesupplement.client.animation;

import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

public final class FirstPersonSprintJumpAnimator {
    private static final float MIN_DELTA_SECONDS = 1.0F / 240.0F;
    private static final float MAX_DELTA_SECONDS = 0.05F;
    private static final float JUMP_VELOCITY = 0.42F;
    private static final float FALL_VELOCITY = 0.62F;

    private static long lastUpdateTime = -1L;
    private static boolean lastOnGround = true;
    private static float lastVerticalVelocity;
    private static float sprintBlend;
    private static float airborneBlend;
    private static float landingImpulse;
    private static float pitch;
    private static float verticalOffset;
    private static float forwardOffset;

    private FirstPersonSprintJumpAnimator() {
    }

    public static void apply(LocalPlayer player, PoseStack poseStack) {
        if (player == null || !TaczSupplementConfig.isSprintJumpAnimationEnabled()) {
            reset();
            return;
        }

        float deltaSeconds = getDeltaSeconds();
        boolean onGround = player.onGround();
        float horizontalSpeed = (float) player.getDeltaMovement().horizontalDistance();
        boolean sprinting = player.isSprinting() && !player.isPassenger() && !player.isFallFlying();

        float sprintTarget = sprinting ? Mth.clamp(horizontalSpeed / 0.13F, 0.0F, 1.0F) : 0.0F;
        sprintBlend = easeTo(sprintBlend, sprintTarget, deltaSeconds, sprintTarget > sprintBlend ? 11.0F : 8.0F);

        float airborneTarget = sprintBlend > 0.05F && !onGround ? 1.0F : 0.0F;
        airborneBlend = easeTo(airborneBlend, airborneTarget, deltaSeconds, airborneTarget > airborneBlend ? 10.0F : 13.0F);

        if (!lastOnGround && onGround && sprintBlend > 0.1F) {
            landingImpulse = Math.max(landingImpulse, Mth.clamp(-lastVerticalVelocity / 0.8F, 0.0F, 1.0F));
        }
        landingImpulse = easeTo(landingImpulse, 0.0F, deltaSeconds, onGround ? 7.5F : 3.5F);

        float verticalVelocity = (float) player.getDeltaMovement().y;
        float rise = Mth.clamp(verticalVelocity / JUMP_VELOCITY, 0.0F, 1.0F) * airborneBlend;
        float fall = Mth.clamp(-verticalVelocity / FALL_VELOCITY, 0.0F, 1.0F) * airborneBlend;

        float targetPitch = ((-rise * 0.16F) + (fall * 0.05F) + (landingImpulse * 0.10F)) * sprintBlend;
        float targetVerticalOffset = ((-rise * 0.012F) + (fall * 0.004F) - (landingImpulse * 0.009F)) * sprintBlend;
        float targetForwardOffset = ((-rise * 0.018F) + (fall * 0.006F) - (landingImpulse * 0.012F)) * sprintBlend;

        pitch = easeTo(pitch, targetPitch, deltaSeconds, 12.0F);
        verticalOffset = easeTo(verticalOffset, targetVerticalOffset, deltaSeconds, 14.0F);
        forwardOffset = easeTo(forwardOffset, targetForwardOffset, deltaSeconds, 13.0F);

        if (Math.abs(verticalOffset) > 1.0E-4F || Math.abs(forwardOffset) > 1.0E-4F) {
            poseStack.translate(0.0F, verticalOffset, forwardOffset);
        }
        if (Math.abs(pitch) > 1.0E-4F) {
            poseStack.mulPose(Axis.XP.rotation(pitch));
        }

        lastOnGround = onGround;
        lastVerticalVelocity = verticalVelocity;
    }

    public static void reset() {
        lastUpdateTime = -1L;
        lastOnGround = true;
        lastVerticalVelocity = 0.0F;
        sprintBlend = 0.0F;
        airborneBlend = 0.0F;
        landingImpulse = 0.0F;
        pitch = 0.0F;
        verticalOffset = 0.0F;
        forwardOffset = 0.0F;
    }

    private static float getDeltaSeconds() {
        long now = System.currentTimeMillis();
        if (lastUpdateTime == -1L) {
            lastUpdateTime = now;
            return 1.0F / 60.0F;
        }
        float deltaSeconds = (now - lastUpdateTime) / 1000.0F;
        lastUpdateTime = now;
        return Mth.clamp(deltaSeconds, MIN_DELTA_SECONDS, MAX_DELTA_SECONDS);
    }

    private static float easeTo(float current, float target, float deltaSeconds, float response) {
        float blend = 1.0F - (float) Math.exp(-response * deltaSeconds);
        return Mth.lerp(blend, current, target);
    }
}
