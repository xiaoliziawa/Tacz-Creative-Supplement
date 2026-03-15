package com.lirxowo.taczcreativesupplement.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TaczSupplementConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_CREATIVE_SUPPLEMENT;
    public static final ForgeConfigSpec.EnumValue<GameModeOption> GAME_MODE;

    static {
        BUILDER.comment("TaczCreativeSupplement Client Configuration");

        ENABLE_CREATIVE_SUPPLEMENT = BUILDER
                .comment("Enable or disable the attachment supplement feature entirely.")
                .define("enableCreativeSupplement", true);

        GAME_MODE = BUILDER
                .comment(
                        "Which game mode(s) the feature applies to.",
                        "CREATIVE = Creative mode only (default)",
                        "SURVIVAL = Survival mode only",
                        "BOTH = Both Creative and Survival modes"
                )
                .defineEnum("gameMode", GameModeOption.CREATIVE);

        CLIENT_SPEC = BUILDER.build();
    }

    public static boolean isPlayerAllowed(boolean isCreative) {
        if (!CLIENT_SPEC.isLoaded()) {
            return isCreative;
        }
        if (!ENABLE_CREATIVE_SUPPLEMENT.get()) return false;
        GameModeOption mode = GAME_MODE.get();
        if (mode == GameModeOption.CREATIVE) return isCreative;
        if (mode == GameModeOption.SURVIVAL) return !isCreative;
        return true; // BOTH
    }
}
