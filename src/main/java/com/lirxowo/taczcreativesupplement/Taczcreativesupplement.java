package com.lirxowo.taczcreativesupplement;

import com.lirxowo.taczcreativesupplement.client.ClientEventHandler;
import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.lirxowo.taczcreativesupplement.network.ModNetworkHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Taczcreativesupplement.MODID)
public class Taczcreativesupplement {

    public static final String MODID = "taczcreativesupplement";

    public Taczcreativesupplement(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, TaczSupplementConfig.CLIENT_SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, TaczSupplementConfig.COMMON_SPEC);
        modEventBus.addListener(ModNetworkHandler::register);
        if (FMLEnvironment.dist.isClient()) {
            ClientEventHandler.registerConfigScreen(modContainer);
        }
    }
}
