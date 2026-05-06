package com.elfmcys.yesstevemodel;

import io.netty.channel.Channel;
import java.util.Objects;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class OO00O0OOoo00O00O00O0oooO implements
    StreamCodec<RegistryFriendlyByteBuf, OO00O0OOoo00O00O00O0oooO.oo0Oo0oo0OOO00O0oO0o0O0O> {
    private final CustomPacketPayload.Type<oo0Oo0oo0OOO00O0oO0o0O0O> OoO0O0oO00O0o0OOOOoOOooo;

    public OO00O0OOoo00O00O00O0oooO(Identifier channelId) {
        this.OoO0O0oO00O0o0OOOOoOOooo = new CustomPacketPayload.Type<>(Objects.requireNonNull(channelId, "channelId"));
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(
        int id,
        Class<MSG> messageType,
        o000ooo0oO0O0ooOO00oo0o0<MSG> serializer,
        oOoOoO0OoOOoo00ooO0oO00o<MSG> deserializer,
        OoO0O0oO00O0o0OOOOoOOooo<MSG> handler
    ) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(
        int id,
        Class<MSG> messageType,
        o000ooo0oO0O0ooOO00oo0o0<MSG> serializer,
        oOoOoO0OoOOoo00ooO0oO00o<MSG> deserializer,
        oOo0O00Oooo00OOO0OO0oooo<MSG> handler
    ) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(
        int id,
        Class<MSG> messageType,
        o000ooo0oO0O0ooOO00oo0o0<MSG> serializer,
        oOoOoO0OoOOoo00ooO0oO00o<MSG> deserializer,
        oOo0O00Oooo00OOO0OO0oooo<MSG> handler,
        PacketFlow side
    ) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(MSG message) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(ServerPlayer player, MSG message) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(Iterable<ServerPlayer> players, MSG message) {
    }

    public <MSG> void oo0Oo0oo0OOO00O0oO0o0O0O(ServerPlayer player, MSG message) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(Entity entity, MSG message) {
    }

    public <MSG> void oo0Oo0oo0OOO00O0oO0o0O0O(MSG message) {
    }

    public <MSG> Packet<?> OoO0O0oO00O0o0OOOOoOOooo(MSG message, PacketFlow side) {
        return null;
    }

    public CustomPacketPayload.Type<oo0Oo0oo0OOO00O0oO0o0O0O> OoO0O0oO00O0o0OOOOoOOooo() {
        return this.OoO0O0oO00O0o0OOOOoOOooo;
    }

    @Override
    public oo0Oo0oo0OOO00O0oO0o0O0O decode(RegistryFriendlyByteBuf buf) {
        buf.skipBytes(buf.readableBytes());
        return new oo0Oo0oo0OOO00O0oO0o0O0O(this.OoO0O0oO00O0o0OOOOoOOooo);
    }

    public oo0Oo0oo0OOO00O0oO0o0O0O OoO0O0oO00O0o0OOOOoOOooo(RegistryFriendlyByteBuf buf) {
        return this.decode(buf);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, oo0Oo0oo0OOO00O0oO0o0O0O payload) {
    }

    public void OoO0O0oO00O0o0OOOOoOOooo(RegistryFriendlyByteBuf buf, oo0Oo0oo0OOO00O0oO0o0O0O payload) {
        this.encode(buf, payload);
    }

    public interface OoO0O0oO00O0o0OOOOoOOooo<MSG> extends oOo0O00Oooo00OOO0OO0oooo<MSG> {
        default void handle(MSG message, Player player, Connection connection) {
        }

        void handleOnServer(MSG message, ServerPlayer player, Connection connection);
    }

    public interface o000ooo0oO0O0ooOO00oo0o0<MSG> {
        void serialize(MSG message, RegistryFriendlyByteBuf buf);
    }

    public interface oOo0O00Oooo00OOO0OO0oooo<MSG> {
        void handle(MSG message, Player player, Connection connection);
    }

    public interface oOoOoO0OoOOoo00ooO0oO00o<MSG> {
        MSG deserialize(RegistryFriendlyByteBuf buf);
    }

    public record oo0Oo0oo0OOO00O0oO0o0O0O(
        CustomPacketPayload.Type<oo0Oo0oo0OOO00O0oO0o0O0O> OoO0O0oO00O0o0OOOOoOOooo
    ) implements CustomPacketPayload {
        @Override
        public CustomPacketPayload.Type<oo0Oo0oo0OOO00O0oO0o0O0O> type() {
            return this.OoO0O0oO00O0o0OOOOoOOooo;
        }
    }
}
