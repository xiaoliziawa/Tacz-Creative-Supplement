package com.lirxowo.taczcreativesupplement.client;

import com.lirxowo.taczcreativesupplement.client.screen.ModConfigScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ClientEventHandler {

    public static void registerConfigScreen(net.neoforged.fml.ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (container, parentScreen) -> new ModConfigScreen(parentScreen));
    }
}
