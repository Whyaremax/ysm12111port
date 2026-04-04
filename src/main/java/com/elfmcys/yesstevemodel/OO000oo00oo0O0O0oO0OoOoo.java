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

public final class OO000oo00oo0O0O0oO0OoOoo {
    public static final String OoO0O0oO00O0o0OOOOoOOooo = "2.6.0";
    public static final Identifier oo0Oo0oo0OOO00O0oO0o0O0O = Identifier.of("yes_steve_model", "2_6_0");
    public static final OO00O0OOoo00O00O00O0oooO oOoOoO0OoOOoo00ooO0oO00o =
        new OO00O0OOoo00O00O00O0oooO(oo0Oo0oo0OOO00O0oO0o0O0O);
    private static boolean O0o0O00O0oOOoOOoo00OO0o0;

    private static final AttributeKey<String> o0o0O0OOo0O0O0oO00oooO00 =
        AttributeKey.valueOf("yes_steve_model_channel_version");

    private OO000oo00oo0O0O0oO0OoOoo() {
    }

    public static boolean OoO0O0oO00O0o0OOOOoOOooo(ClientConnection connection, String version) {
        Channel channel = ((ConnectionAccessor) connection).OoO0O0oO00O0o0OOOOoOOooo();
        return channel.attr(o0o0O0OOo0O0O0oO00oooO00).compareAndSet(null, version);
    }

    public static boolean OoO0O0oO00O0o0OOOOoOOooo(ServerPlayerEntity player) {
        if (player.networkHandler == null) {
            return false;
        }
        return OoO0O0oO00O0o0OOOOoOOooo(((ServerGameConnectionAccessor) player.networkHandler).OoO0O0oO00O0o0OOOOoOOooo());
    }

    public static boolean OoO0O0oO00O0o0OOOOoOOooo() {
        return false;
    }

    public static boolean OoO0O0oO00O0o0OOOOoOOooo(ClientConnection connection) {
        if (connection == null) {
            return false;
        }
        Channel channel = ((ConnectionAccessor) connection).OoO0O0oO00O0o0OOOOoOOooo();
        return OoO0O0oO00O0o0OOOOoOOooo.equals(channel.attr(o0o0O0OOo0O0O0oO00oooO00).get());
    }

    public static void oo0Oo0oo0OOO00O0oO0o0O0O() {
        if (O0o0O00O0oOOoOOoo00OO0o0) {
            return;
        }

        PayloadTypeRegistry.playC2S().register(
            oOoOoO0OoOOoo00ooO0oO00o.OoO0O0oO00O0o0OOOOoOOooo(),
            oOoOoO0OoOOoo00ooO0oO00o
        );
        PayloadTypeRegistry.playS2C().register(
            oOoOoO0OoOOoo00ooO0oO00o.OoO0O0oO00O0o0OOOOoOOooo(),
            oOoOoO0OoOOoo00ooO0oO00o
        );
        O0o0O00O0oOOoOOoo00OO0o0 = true;
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(Object message) {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(Object message, ServerPlayerEntity player) {
    }

    public static void oo0Oo0oo0OOO00O0oO0o0O0O(Object message) {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(Object message, Entity entity) {
    }

    public static void oo0Oo0oo0OOO00O0oO0o0O0O(Object message, ServerPlayerEntity player) {
    }
}
