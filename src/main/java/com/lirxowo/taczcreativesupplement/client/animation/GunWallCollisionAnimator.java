package com.lirxowo.taczcreativesupplement.client.animation;

import com.lirxowo.taczcreativesupplement.gameplay.GunWallCollisionHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class GunWallCollisionAnimator {
    private static final float MIN_DELTA_SECONDS = 1.0F / 240.0F;
    private static final float MAX_DELTA_SECONDS = 0.05F;
    private static final long STATE_EXPIRE_MS = 15000L;
    private static final long HINT_INTERVAL_MS = 350L;

    private static final Map<UUID, PoseState> STATES = new HashMap<>();
    private static long lastHintTimestamp = -1L;

    private GunWallCollisionAnimator() {
    }

    public static void applyFirstPerson(LocalPlayer player, PoseStack poseStack) {
        float progress = getProgress(player);
        if (progress <= 1.0E-4F) {
            return;
        }

        float sign = player.getMainArm() == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float eased = easeOutCubic(progress);
        float chestBlend = eased * eased;

        // Tarkov-like wall contact should feel like the weapon is tucked back into the chest,
        // so keep screen-plane roll small and use yaw/pitch as the primary motion.
        poseStack.translate(0.05F * sign * chestBlend, 0.18F * chestBlend, -0.10F * chestBlend);
        poseStack.mulPose(Axis.YP.rotationDegrees(-74.0F * sign * chestBlend));
        poseStack.mulPose(Axis.XP.rotationDegrees(18.0F * chestBlend));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-12.0F * sign * chestBlend));
        poseStack.translate(-0.02F * sign * chestBlend, 0.04F * chestBlend, 0.10F * eased);
    }

    public static void applyThirdPersonPose(LivingEntity entity, ModelPart rightArm, ModelPart leftArm, ModelPart body, ModelPart head) {
        if (entity == null || isLocalFirstPerson(entity)) {
            return;
        }

        float progress = getProgress(entity);
        if (progress <= 1.0E-4F) {
            return;
        }

        PlayerAnimatorCompat.stopAllAnimation(entity);

        boolean rightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
        ModelPart mainArm = rightHanded ? rightArm : leftArm;
        ModelPart offArm = rightHanded ? leftArm : rightArm;
        float sign = rightHanded ? 1.0F : -1.0F;

        body.yRot = Mth.lerp(progress, body.yRot, -0.24F * sign);

        mainArm.xRot = Mth.lerp(progress, mainArm.xRot, -1.18F);
        mainArm.yRot = Mth.lerp(progress, mainArm.yRot, -0.95F * sign);
        mainArm.zRot = Mth.lerp(progress, mainArm.zRot, 0.70F * sign);

        offArm.xRot = Mth.lerp(progress, offArm.xRot, -1.42F);
        offArm.yRot = Mth.lerp(progress, offArm.yRot, -0.25F * sign);
        offArm.zRot = Mth.lerp(progress, offArm.zRot, -0.18F * sign);
    }

    public static void showBlockedHint(LocalPlayer player) {
        long now = System.currentTimeMillis();
        if (lastHintTimestamp != -1L && now - lastHintTimestamp < HINT_INTERVAL_MS) {
            return;
        }
        lastHintTimestamp = now;
        player.displayClientMessage(Component.translatable("message.taczcreativesupplement.wall_blocked"), true);
    }

    public static void reset() {
        STATES.clear();
        lastHintTimestamp = -1L;
    }

    private static float getProgress(LivingEntity entity) {
        if (entity == null) {
            return 0.0F;
        }

        UUID id = entity.getUUID();
        PoseState state = STATES.computeIfAbsent(id, key -> new PoseState());
        long now = System.currentTimeMillis();
        pruneOldStates(now);

        float deltaSeconds = state.lastUpdateTime == -1L
                ? 1.0F / 60.0F
                : Mth.clamp((now - state.lastUpdateTime) / 1000.0F, MIN_DELTA_SECONDS, MAX_DELTA_SECONDS);

        float target = GunWallCollisionHelper.isHoldingGun(entity) ? GunWallCollisionHelper.getBlockStrength(entity) : 0.0F;
        float response = target > state.progress ? 18.0F : 11.0F;
        float blend = 1.0F - (float) Math.exp(-response * deltaSeconds);

        state.progress = Mth.lerp(blend, state.progress, target);
        state.lastUpdateTime = now;
        state.lastAccessTime = now;
        return state.progress;
    }

    private static void pruneOldStates(long now) {
        Iterator<Map.Entry<UUID, PoseState>> iterator = STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PoseState> entry = iterator.next();
            PoseState state = entry.getValue();
            if (state.lastAccessTime != -1L && now - state.lastAccessTime > STATE_EXPIRE_MS) {
                iterator.remove();
            }
        }
    }

    private static boolean isLocalFirstPerson(LivingEntity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player == entity && minecraft.options.getCameraType() == CameraType.FIRST_PERSON;
    }

    private static float easeOutCubic(float progress) {
        float inverse = 1.0F - progress;
        return 1.0F - inverse * inverse * inverse;
    }

    private static final class PoseState {
        private float progress;
        private long lastUpdateTime = -1L;
        private long lastAccessTime = -1L;
    }
}
