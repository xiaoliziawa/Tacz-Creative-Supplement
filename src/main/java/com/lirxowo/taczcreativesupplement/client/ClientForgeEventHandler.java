package com.lirxowo.taczcreativesupplement.client;

import com.lirxowo.taczcreativesupplement.client.animation.FirstPersonSprintJumpAnimator;
import com.lirxowo.taczcreativesupplement.Taczcreativesupplement;
import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Taczcreativesupplement.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientForgeEventHandler {
    private ClientForgeEventHandler() {
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        FirstPersonSprintJumpAnimator.reset();
        TaczSupplementConfig.clearSyncedGameRuleState();
    }
}
