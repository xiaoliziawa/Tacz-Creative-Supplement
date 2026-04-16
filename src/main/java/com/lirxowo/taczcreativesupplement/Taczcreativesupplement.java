package com.lirxowo.taczcreativesupplement;

import com.lirxowo.taczcreativesupplement.client.ClientEventHandler;
import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.lirxowo.taczcreativesupplement.gamerule.ModGameRules;
import com.lirxowo.taczcreativesupplement.network.ModNetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Taczcreativesupplement.MODID)
public class Taczcreativesupplement {

    public static final String MODID = "taczcreativesupplement";

    public Taczcreativesupplement(FMLJavaModLoadingContext context) {
        ModGameRules.init();
        context.registerConfig(ModConfig.Type.CLIENT, TaczSupplementConfig.CLIENT_SPEC);
        context.registerConfig(ModConfig.Type.COMMON, TaczSupplementConfig.COMMON_SPEC);
        context.getModEventBus().addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientEventHandler.registerExtensionPoints(context));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetworkHandler.init();
    }
}
