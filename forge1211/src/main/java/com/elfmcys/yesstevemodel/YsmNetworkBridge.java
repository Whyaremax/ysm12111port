package com.elfmcys.yesstevemodel;

import io.netty.util.AttributeKey;
import net.minecraft.network.Connection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class YsmNetworkBridge {
    public static final String PROTOCOL_VERSION = "2.6.0";
    public static final Identifier CHANNEL_ID = Identifier.fromNamespaceAndPath(YesSteveModel.MOD_ID, "2_6_0");
    public static final YsmPayloadChannel PAYLOAD_CHANNEL = new YsmPayloadChannel(CHANNEL_ID);

    @Deprecated
    public static final OO00O0OOoo00O00O00O0oooO LEGACY_PAYLOAD_CHANNEL =
        new OO00O0OOoo00O00O00O0oooO(CHANNEL_ID);

    private static final AttributeKey<String> CHANNEL_VERSION_ATTRIBUTE =
        AttributeKey.valueOf("yes_steve_model_channel_version");

    private static boolean payloadTypesRegistered;

    private YsmNetworkBridge() {
    }

    public static boolean rememberConnectionVersion(Connection connection, String version) {
        return false;
    }

    public static boolean playerSupportsYsmChannel(ServerPlayer player) {
        return false;
    }

    public static boolean localConnectionSupportsYsmChannel() {
        return false;
    }

    public static boolean connectionSupportsYsmChannel(Connection connection) {
        if (connection == null) {
            return false;
        }
        return false;
    }

    public static void registerPayloadTypes() {
        if (payloadTypesRegistered) {
            return;
        }

        // Client-focused Forge lane: keep the channel object available for
        // compatibility callers, but do not register Fabric payload types.
        payloadTypesRegistered = true;
    }

    public static void sendToServer(Object message) {
    }

    public static void sendToPlayer(Object message, ServerPlayer player) {
    }

    public static void broadcastToClients(Object message) {
    }

    public static void sendToTrackingEntity(Object message, Entity entity) {
    }

    public static void sendToPlayerTracking(Object message, ServerPlayer player) {
    }
}
