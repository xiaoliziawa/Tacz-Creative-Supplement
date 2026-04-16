package com.lirxowo.taczcreativesupplement.config;

import com.lirxowo.taczcreativesupplement.gamerule.ModGameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.ForgeConfigSpec;

public class TaczSupplementConfig {
    private static volatile Boolean syncedGameRuleEnabled;
    private static volatile GameModeOption syncedGameRuleMode;

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_CREATIVE_SUPPLEMENT;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPRINT_JUMP_ANIMATION;
    public static final ForgeConfigSpec.EnumValue<GameModeOption> GAME_MODE;

    static {
        CLIENT_BUILDER.comment("TaczCreativeSupplement Client Configuration");

        ENABLE_CREATIVE_SUPPLEMENT = CLIENT_BUILDER
                .comment("Enable or disable the attachment supplement feature entirely.")
                .define("enableCreativeSupplement", true);

        ENABLE_SPRINT_JUMP_ANIMATION = CLIENT_BUILDER
                .comment("Smooth the first-person sprint jump gun animation.")
                .define("enableSprintJumpAnimation", true);

        GAME_MODE = CLIENT_BUILDER
                .comment(
                        "Which game mode(s) the feature applies to.",
                        "CREATIVE = Creative mode only (default)",
                        "SURVIVAL = Survival mode only",
                        "BOTH = Creative and Survival modes",
                        "ADVENTURE = Adventure mode only",
                        "ADVENTURE_CREATIVE = Adventure and Creative modes"
                )
                .defineEnum("gameMode", GameModeOption.CREATIVE);

        CLIENT_SPEC = CLIENT_BUILDER.build();
    }

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_SPEC;

    public static final ForgeConfigSpec.BooleanValue COMMON_ENABLE;
    public static final ForgeConfigSpec.EnumValue<GameModeOption> COMMON_GAME_MODE;

    static {
        COMMON_BUILDER.comment(
                "TaczCreativeSupplement Common Configuration",
                "Used by dedicated servers where CLIENT config is not available.",
                "On single-player / LAN, gamerules take priority once the server sync arrives."
        );

        COMMON_ENABLE = COMMON_BUILDER
                .comment("Enable or disable the attachment supplement feature on the server.")
                .define("enableCreativeSupplement", true);

        COMMON_GAME_MODE = COMMON_BUILDER
                .comment(
                        "Which game mode(s) the feature applies to on the server.",
                        "CREATIVE = Creative mode only (default)",
                        "SURVIVAL = Survival mode only",
                        "BOTH = Creative and Survival modes",
                        "ADVENTURE = Adventure mode only",
                        "ADVENTURE_CREATIVE = Adventure and Creative modes"
                )
                .defineEnum("gameMode", GameModeOption.CREATIVE);

        COMMON_SPEC = COMMON_BUILDER.build();
    }

    public static boolean isPlayerAllowed(GameType gameType) {
        if (hasSyncedGameRuleState()) {
            return checkConfig(syncedGameRuleEnabled, syncedGameRuleMode, gameType);
        }
        if (CLIENT_SPEC.isLoaded()) {
            return checkConfig(ENABLE_CREATIVE_SUPPLEMENT.get(), GAME_MODE.get(), gameType);
        }
        if (COMMON_SPEC.isLoaded()) {
            return checkConfig(COMMON_ENABLE.get(), COMMON_GAME_MODE.get(), gameType);
        }
        return gameType == GameType.CREATIVE;
    }

    public static boolean isPlayerAllowed(GameRules gameRules, GameType gameType) {
        return checkConfig(
                ModGameRules.isEnabled(gameRules),
                ModGameRules.getGameMode(gameRules),
                gameType
        );
    }

    public static boolean isSprintJumpAnimationEnabled() {
        if (!CLIENT_SPEC.isLoaded()) {
            return true;
        }
        return ENABLE_SPRINT_JUMP_ANIMATION.get();
    }

    public static void updateSyncedGameRuleState(boolean enabled, GameModeOption mode) {
        syncedGameRuleEnabled = enabled;
        syncedGameRuleMode = mode;
    }

    public static void clearSyncedGameRuleState() {
        syncedGameRuleEnabled = null;
        syncedGameRuleMode = null;
    }

    public static boolean hasSyncedGameRuleState() {
        return syncedGameRuleEnabled != null && syncedGameRuleMode != null;
    }

    private static boolean checkConfig(boolean enabled, GameModeOption mode, GameType gameType) {
        if (!enabled) return false;
        return mode.matches(gameType);
    }
}
