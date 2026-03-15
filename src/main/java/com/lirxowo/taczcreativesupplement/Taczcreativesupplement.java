package com.lirxowo.taczcreativesupplement;

import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.lirxowo.taczcreativesupplement.network.ModNetworkHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Taczcreativesupplement.MODID)
public class Taczcreativesupplement {

    public static final String MODID = "taczcreativesupplement";

    public Taczcreativesupplement() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TaczSupplementConfig.CLIENT_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetworkHandler.init();
    }
}
