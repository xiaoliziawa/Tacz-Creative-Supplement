package com.lirxowo.taczcreativesupplement.gamerule;

import com.lirxowo.taczcreativesupplement.config.GameModeOption;
import com.lirxowo.taczcreativesupplement.network.ModNetworkHandler;
import com.lirxowo.taczcreativesupplement.network.SyncGameRuleStateMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.PacketDistributor;

public final class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> RULE_ENABLED = GameRules.register(
            "taczCreativeSupplementEnabled",
            GameRules.Category.PLAYER,
            GameRules.BooleanValue.create(true, ModGameRules::onRuleChanged)
    );
    public static final GameRules.Key<GameRules.IntegerValue> RULE_MODE = GameRules.register(
            "taczCreativeSupplementMode",
            GameRules.Category.PLAYER,
            GameRules.IntegerValue.create(GameModeOption.CREATIVE.toGameRuleValue(), ModGameRules::onModeChanged)
    );

    private ModGameRules() {
    }

    public static void init() {
    }

    public static boolean isEnabled(GameRules gameRules) {
        return gameRules.getBoolean(RULE_ENABLED);
    }

    public static GameModeOption getGameMode(GameRules gameRules) {
        return GameModeOption.fromGameRuleValue(gameRules.getInt(RULE_MODE));
    }

    public static void syncToPlayer(ServerPlayer player) {
        ModNetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                SyncGameRuleStateMessage.from(player.serverLevel().getGameRules())
        );
    }

    private static void onRuleChanged(MinecraftServer server, GameRules.BooleanValue value) {
        syncToAll(server);
    }

    private static void onModeChanged(MinecraftServer server, GameRules.IntegerValue value) {
        int normalized = GameModeOption.normalizeGameRuleValue(value.get());
        if (normalized != value.get()) {
            value.set(normalized, null);
        }
        syncToAll(server);
    }

    private static void syncToAll(MinecraftServer server) {
        SyncGameRuleStateMessage message = SyncGameRuleStateMessage.from(server.getGameRules());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ModNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
        }
    }
}
