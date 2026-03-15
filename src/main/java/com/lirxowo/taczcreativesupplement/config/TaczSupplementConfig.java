package com.lirxowo.taczcreativesupplement.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TaczSupplementConfig {

    // 给单人（包括局域网联机）用的配置文件
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_CREATIVE_SUPPLEMENT;
    public static final ForgeConfigSpec.EnumValue<GameModeOption> GAME_MODE;

    static {
        CLIENT_BUILDER.comment("TaczCreativeSupplement Client Configuration");

        ENABLE_CREATIVE_SUPPLEMENT = CLIENT_BUILDER
                .comment("Enable or disable the attachment supplement feature entirely.")
                .define("enableCreativeSupplement", true);

        GAME_MODE = CLIENT_BUILDER
                .comment(
                        "Which game mode(s) the feature applies to.",
                        "CREATIVE = Creative mode only (default)",
                        "SURVIVAL = Survival mode only",
                        "BOTH = Both Creative and Survival modes"
                )
                .defineEnum("gameMode", GameModeOption.CREATIVE);

        CLIENT_SPEC = CLIENT_BUILDER.build();
    }

    // 给服务器用的配置文件
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_SPEC;

    public static final ForgeConfigSpec.BooleanValue COMMON_ENABLE;
    public static final ForgeConfigSpec.EnumValue<GameModeOption> COMMON_GAME_MODE;

    static {
        COMMON_BUILDER.comment(
                "TaczCreativeSupplement Common Configuration",
                "Used by dedicated servers where CLIENT config is not available.",
                "On single-player / LAN, CLIENT config takes priority over this."
        );

        COMMON_ENABLE = COMMON_BUILDER
                .comment("Enable or disable the attachment supplement feature on the server.")
                .define("enableCreativeSupplement", true);

        COMMON_GAME_MODE = COMMON_BUILDER
                .comment(
                        "Which game mode(s) the feature applies to on the server.",
                        "CREATIVE = Creative mode only (default)",
                        "SURVIVAL = Survival mode only",
                        "BOTH = Both Creative and Survival modes"
                )
                .defineEnum("gameMode", GameModeOption.CREATIVE);

        COMMON_SPEC = COMMON_BUILDER.build();
    }

    public static boolean isPlayerAllowed(boolean isCreative) {
        if (CLIENT_SPEC.isLoaded()) {
            return checkConfig(ENABLE_CREATIVE_SUPPLEMENT.get(), GAME_MODE.get(), isCreative);
        }
        if (COMMON_SPEC.isLoaded()) {
            return checkConfig(COMMON_ENABLE.get(), COMMON_GAME_MODE.get(), isCreative);
        }
        return isCreative;
    }

    private static boolean checkConfig(boolean enabled, GameModeOption mode, boolean isCreative) {
        if (!enabled) return false;
        if (mode == GameModeOption.CREATIVE) return isCreative;
        if (mode == GameModeOption.SURVIVAL) return !isCreative;
        return true; // BOTH
    }
}
