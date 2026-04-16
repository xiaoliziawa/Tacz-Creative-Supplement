package com.lirxowo.taczcreativesupplement.client;

import com.lirxowo.taczcreativesupplement.client.animation.FirstPersonSprintJumpAnimator;
import com.lirxowo.taczcreativesupplement.client.animation.GunWallCollisionAnimator;
import com.lirxowo.taczcreativesupplement.gameplay.GunWallCollisionHelper;
import com.lirxowo.taczcreativesupplement.Taczcreativesupplement;
import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.tacz.guns.api.event.common.GunShootEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Taczcreativesupplement.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientForgeEventHandler {
    private ClientForgeEventHandler() {
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        FirstPersonSprintJumpAnimator.reset();
        GunWallCollisionAnimator.reset();
        TaczSupplementConfig.clearSyncedGameRuleState();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onClientGunShoot(GunShootEvent event) {
        if (!event.isCanceled() || !event.getLogicalSide().isClient()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || event.getShooter() != player) {
            return;
        }
        if (GunWallCollisionHelper.isBlocked(player)) {
            GunWallCollisionAnimator.showBlockedHint(player);
        }
    }
}
