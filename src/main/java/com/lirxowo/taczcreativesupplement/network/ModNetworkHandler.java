package com.lirxowo.taczcreativesupplement.network;

import com.lirxowo.taczcreativesupplement.Taczcreativesupplement;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetworkHandler {
    private static final String VERSION = "1.0";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Taczcreativesupplement.MODID, "network"),
            () -> VERSION,
            VERSION::equals,
            VERSION::equals
    );

    public static void init() {
        CHANNEL.registerMessage(0, CreativeRefitMessage.class,
                CreativeRefitMessage::encode,
                CreativeRefitMessage::decode,
                CreativeRefitMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(1, SyncGameRuleStateMessage.class,
                SyncGameRuleStateMessage::encode,
                SyncGameRuleStateMessage::decode,
                SyncGameRuleStateMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
