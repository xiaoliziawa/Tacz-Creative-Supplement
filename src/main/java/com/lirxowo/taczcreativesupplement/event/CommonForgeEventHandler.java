package com.lirxowo.taczcreativesupplement.event;

import com.lirxowo.taczcreativesupplement.Taczcreativesupplement;
import com.lirxowo.taczcreativesupplement.gamerule.ModGameRules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Taczcreativesupplement.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonForgeEventHandler {
    private CommonForgeEventHandler() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModGameRules.syncToPlayer(player);
        }
    }
}
