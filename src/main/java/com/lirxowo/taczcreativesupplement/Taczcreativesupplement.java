package com.lirxowo.taczcreativesupplement;

import com.lirxowo.taczcreativesupplement.network.ModNetworkHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Taczcreativesupplement.MODID)
public class Taczcreativesupplement {

    public static final String MODID = "taczcreativesupplement";

    public Taczcreativesupplement() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetworkHandler.init();
    }
}
