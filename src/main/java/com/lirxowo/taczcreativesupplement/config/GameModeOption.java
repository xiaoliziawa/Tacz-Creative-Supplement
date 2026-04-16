package com.lirxowo.taczcreativesupplement.config;

import net.minecraft.world.level.GameType;

public enum GameModeOption {
    CREATIVE(0),
    SURVIVAL(1),
    BOTH(2),
    ADVENTURE(3),
    ADVENTURE_CREATIVE(4);

    private final int gameRuleValue;

    GameModeOption(int gameRuleValue) {
        this.gameRuleValue = gameRuleValue;
    }

    public int toGameRuleValue() {
        return gameRuleValue;
    }

    public static int normalizeGameRuleValue(int value) {
        return switch (value) {
            case 0, 1, 2, 3, 4 -> value;
            default -> CREATIVE.gameRuleValue;
        };
    }

    public static GameModeOption fromGameRuleValue(int value) {
        return switch (normalizeGameRuleValue(value)) {
            case 1 -> SURVIVAL;
            case 2 -> BOTH;
            case 3 -> ADVENTURE;
            case 4 -> ADVENTURE_CREATIVE;
            default -> CREATIVE;
        };
    }

    public boolean matches(GameType gameType) {
        if (gameType == null) {
            return false;
        }

        return switch (this) {
            case CREATIVE -> gameType == GameType.CREATIVE;
            case SURVIVAL -> gameType == GameType.SURVIVAL;
            case BOTH -> gameType == GameType.CREATIVE || gameType == GameType.SURVIVAL;
            case ADVENTURE -> gameType == GameType.ADVENTURE;
            case ADVENTURE_CREATIVE -> gameType == GameType.ADVENTURE || gameType == GameType.CREATIVE;
        };
    }
}
