package com.elfmcys.yesstevemodel;

import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Deprecated
public final class OO000oo00oo0O0O0oO0OoOoo {
    public static final String OoO0O0oO00O0o0OOOOoOOooo = YsmNetworkBridge.PROTOCOL_VERSION;
    public static final Identifier oo0Oo0oo0OOO00O0oO0o0O0O = YsmNetworkBridge.CHANNEL_ID;
    public static final OO00O0OOoo00O00O00O0oooO oOoOoO0OoOOoo00ooO0oO00o = YsmNetworkBridge.LEGACY_PAYLOAD_CHANNEL;

    private OO000oo00oo0O0O0oO0OoOoo() {
    }

    public static boolean OoO0O0oO00O0o0OOOOoOOooo(ClientConnection connection, String version) {
        return YsmNetworkBridge.rememberConnectionVersion(connection, version);
    }

    public static boolean OoO0O0oO00O0o0OOOOoOOooo(ServerPlayerEntity player) {
        return YsmNetworkBridge.playerSupportsYsmChannel(player);
    }

    public static boolean OoO0O0oO00O0o0OOOOoOOooo() {
        return YsmNetworkBridge.localConnectionSupportsYsmChannel();
    }

    public static boolean OoO0O0oO00O0o0OOOOoOOooo(ClientConnection connection) {
        return YsmNetworkBridge.connectionSupportsYsmChannel(connection);
    }

    public static void oo0Oo0oo0OOO00O0oO0o0O0O() {
        YsmNetworkBridge.registerPayloadTypes();
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(Object message) {
        YsmNetworkBridge.sendToServer(message);
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(Object message, ServerPlayerEntity player) {
        YsmNetworkBridge.sendToPlayer(message, player);
    }

    public static void oo0Oo0oo0OOO00O0oO0o0O0O(Object message) {
        YsmNetworkBridge.broadcastToClients(message);
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(Object message, Entity entity) {
        YsmNetworkBridge.sendToTrackingEntity(message, entity);
    }

    public static void oo0Oo0oo0OOO00O0oO0o0O0O(Object message, ServerPlayerEntity player) {
        YsmNetworkBridge.sendToPlayerTracking(message, player);
    }
}
