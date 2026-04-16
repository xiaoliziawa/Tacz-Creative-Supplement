package com.lirxowo.taczcreativesupplement.gameplay;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;
public final class GunWallCollisionHelper {
    private static final Vec3 UP = new Vec3(0.0D, 1.0D, 0.0D);
    private static final float TRACE_START_OFFSET = 0.08F;
    private static final float SIDE_TRACE_OFFSET = 0.12F;

    private GunWallCollisionHelper() {
    }

    public static boolean isHoldingGun(LivingEntity entity) {
        return entity != null && IGun.getIGunOrNull(entity.getMainHandItem()) != null;
    }

    public static boolean isBlocked(LivingEntity entity) {
        return getBlockStrength(entity) > 0.0F;
    }

    public static float getBlockStrength(LivingEntity entity) {
        if (!canCheck(entity)) {
            return 0.0F;
        }

        ItemStack stack = entity.getMainHandItem();
        IGun gun = IGun.getIGunOrNull(stack);
        if (gun == null) {
            return 0.0F;
        }

        Vec3 look = entity.getViewVector(1.0F);
        if (look.lengthSqr() < 1.0E-6D) {
            return 0.0F;
        }
        look = look.normalize();

        float maxDistance = getCollisionDistance(stack, gun);
        if (maxDistance <= 0.0F) {
            return 0.0F;
        }

        Vec3 right = look.cross(UP);
        if (right.lengthSqr() < 1.0E-6D) {
            right = new Vec3(1.0D, 0.0D, 0.0D);
        }
        right = right.normalize().scale(SIDE_TRACE_OFFSET);

        Vec3 origin = entity.getEyePosition().add(look.scale(TRACE_START_OFFSET));
        float strongest = 0.0F;
        strongest = Math.max(strongest, sampleTrace(entity, origin, look, maxDistance));
        strongest = Math.max(strongest, sampleTrace(entity, origin.add(right), look, maxDistance));
        strongest = Math.max(strongest, sampleTrace(entity, origin.subtract(right), look, maxDistance));
        return strongest;
    }

    private static boolean canCheck(LivingEntity entity) {
        if (entity == null || !entity.isAlive() || entity.isSpectator()) {
            return false;
        }
        return !entity.noPhysics;
    }

    private static float sampleTrace(LivingEntity entity, Vec3 origin, Vec3 look, float maxDistance) {
        Vec3 target = origin.add(look.scale(maxDistance));
        BlockHitResult hitResult = entity.level().clip(new ClipContext(origin, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return 0.0F;
        }
        float hitDistance = (float) hitResult.getLocation().distanceTo(origin);
        float progress = 1.0F - (hitDistance / maxDistance);
        return Mth.clamp(progress, 0.18F, 1.0F);
    }

    private static float getCollisionDistance(ItemStack stack, IGun gun) {
        String type = TimelessAPI.getCommonGunIndex(gun.getGunId(stack))
                .map(CommonGunIndex::getType)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .orElse("");

        float baseDistance;
        switch (type) {
            case "pistol":
            case "revolver":
                baseDistance = 0.45F;
                break;
            case "smg":
            case "pdw":
                baseDistance = 0.58F;
                break;
            case "shotgun":
                baseDistance = 0.82F;
                break;
            case "sniper":
            case "dmr":
            case "lmg":
            case "launcher":
            case "machine_gun":
                baseDistance = 0.92F;
                break;
            case "rifle":
            default:
                baseDistance = 0.74F;
                break;
        }

        if (hasMuzzleAttachment(stack, gun)) {
            baseDistance += 0.08F;
        }
        return baseDistance;
    }

    private static boolean hasMuzzleAttachment(ItemStack stack, IGun gun) {
        ResourceLocation attachmentId = gun.getAttachmentId(stack, AttachmentType.MUZZLE);
        return attachmentId != null && !DefaultAssets.isEmptyAttachmentId(attachmentId);
    }
}
