package com.lirxowo.taczcreativesupplement.network;

import com.lirxowo.taczcreativesupplement.config.GameModeOption;
import com.lirxowo.taczcreativesupplement.config.TaczSupplementConfig;
import com.lirxowo.taczcreativesupplement.gamerule.ModGameRules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGameRuleStateMessage {
    private final boolean enabled;
    private final int gameMode;

    public SyncGameRuleStateMessage(boolean enabled, int gameMode) {
        this.enabled = enabled;
        this.gameMode = GameModeOption.normalizeGameRuleValue(gameMode);
    }

    public static SyncGameRuleStateMessage from(GameRules gameRules) {
        return new SyncGameRuleStateMessage(
                ModGameRules.isEnabled(gameRules),
                ModGameRules.getGameMode(gameRules).toGameRuleValue()
        );
    }

    public static void encode(SyncGameRuleStateMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.enabled);
        buf.writeVarInt(message.gameMode);
    }

    public static SyncGameRuleStateMessage decode(FriendlyByteBuf buf) {
        return new SyncGameRuleStateMessage(buf.readBoolean(), buf.readVarInt());
    }

    public static void handle(SyncGameRuleStateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> TaczSupplementConfig.updateSyncedGameRuleState(
                message.enabled,
                GameModeOption.fromGameRuleValue(message.gameMode)
        ));
        context.setPacketHandled(true);
    }
}
