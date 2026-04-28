package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.mixin.ConnectionAccessor;
import com.elfmcys.yesstevemodel.mixin.ServerGameConnectionAccessor;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class YsmNetworkBridge {
    public static final String PROTOCOL_VERSION = "2.6.0";
    public static final Identifier CHANNEL_ID = Identifier.of(YesSteveModel.MOD_ID, "2_6_0");
    public static final OO00O0OOoo00O00O00O0oooO LEGACY_PAYLOAD_CHANNEL =
        new OO00O0OOoo00O00O00O0oooO(CHANNEL_ID);

    private static final AttributeKey<String> CHANNEL_VERSION_ATTRIBUTE =
        AttributeKey.valueOf("yes_steve_model_channel_version");

    private static boolean payloadTypesRegistered;

    private YsmNetworkBridge() {
    }

    public static boolean rememberConnectionVersion(ClientConnection connection, String version) {
        Channel channel = ((ConnectionAccessor) connection).channel();
        return channel.attr(CHANNEL_VERSION_ATTRIBUTE).compareAndSet(null, version);
    }

    public static boolean playerSupportsYsmChannel(ServerPlayerEntity player) {
        if (player.networkHandler == null) {
            return false;
        }
        return connectionSupportsYsmChannel(((ServerGameConnectionAccessor) player.networkHandler).connection());
    }

    public static boolean localConnectionSupportsYsmChannel() {
        return false;
    }

    public static boolean connectionSupportsYsmChannel(ClientConnection connection) {
        if (connection == null) {
            return false;
        }
        Channel channel = ((ConnectionAccessor) connection).channel();
        return PROTOCOL_VERSION.equals(channel.attr(CHANNEL_VERSION_ATTRIBUTE).get());
    }

    public static void registerPayloadTypes() {
        if (payloadTypesRegistered) {
            return;
        }

        PayloadTypeRegistry.playC2S().register(
            LEGACY_PAYLOAD_CHANNEL.OoO0O0oO00O0o0OOOOoOOooo(),
            LEGACY_PAYLOAD_CHANNEL
        );
        PayloadTypeRegistry.playS2C().register(
            LEGACY_PAYLOAD_CHANNEL.OoO0O0oO00O0o0OOOOoOOooo(),
            LEGACY_PAYLOAD_CHANNEL
        );
        payloadTypesRegistered = true;
    }

    public static void sendToServer(Object message) {
    }

    public static void sendToPlayer(Object message, ServerPlayerEntity player) {
    }

    public static void broadcastToClients(Object message) {
    }

    public static void sendToTrackingEntity(Object message, Entity entity) {
    }

    public static void sendToPlayerTracking(Object message, ServerPlayerEntity player) {
    }
}
