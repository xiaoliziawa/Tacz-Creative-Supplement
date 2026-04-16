package com.lirxowo.taczcreativesupplement.client;

import com.lirxowo.taczcreativesupplement.Taczcreativesupplement;
import com.lirxowo.taczcreativesupplement.client.render.RoundedShaderRenderer;
import com.lirxowo.taczcreativesupplement.client.screen.ModConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = Taczcreativesupplement.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventHandler {

    public static void registerExtensionPoints(ModLoadingContext context) {
        context.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigScreen::new)
        );
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        RoundedShaderRenderer.registerShaders(event);
    }
}
