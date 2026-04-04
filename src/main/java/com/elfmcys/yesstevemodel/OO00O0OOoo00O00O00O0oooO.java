package com.elfmcys.yesstevemodel;

import io.netty.channel.Channel;
import java.util.Objects;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class OO00O0OOoo00O00O00O0oooO implements
    ServerPlayNetworking.PlayPayloadHandler<OO00O0OOoo00O00O00O0oooO.oo0Oo0oo0OOO00O0oO0o0O0O>,
    PacketCodec<RegistryByteBuf, OO00O0OOoo00O00O00O0oooO.oo0Oo0oo0OOO00O0oO0o0O0O> {
    private final CustomPayload.Id<oo0Oo0oo0OOO00O0oO0o0O0O> OoO0O0oO00O0o0OOOOoOOooo;

    public OO00O0OOoo00O00O00O0oooO(Identifier channelId) {
        this.OoO0O0oO00O0o0OOOOoOOooo = new CustomPayload.Id<>(Objects.requireNonNull(channelId, "channelId"));
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
        NetworkSide side
    ) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(MSG message) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(ServerPlayerEntity player, MSG message) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(Iterable<ServerPlayerEntity> players, MSG message) {
    }

    public <MSG> void oo0Oo0oo0OOO00O0oO0o0O0O(ServerPlayerEntity player, MSG message) {
    }

    public <MSG> void OoO0O0oO00O0o0OOOOoOOooo(Entity entity, MSG message) {
    }

    public <MSG> void oo0Oo0oo0OOO00O0oO0o0O0O(MSG message) {
    }

    public <MSG> Packet<?> OoO0O0oO00O0o0OOOOoOOooo(MSG message, NetworkSide side) {
        oo0Oo0oo0OOO00O0oO0o0O0O payload = new oo0Oo0oo0OOO00O0oO0o0O0O(this.OoO0O0oO00O0o0OOOOoOOooo);
        if (side == NetworkSide.CLIENTBOUND) {
            return ServerPlayNetworking.createS2CPacket(payload);
        }

        try {
            return (Packet<?>) Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking")
                .getMethod("createC2SPacket", CustomPayload.class)
                .invoke(null, payload);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    public CustomPayload.Id<oo0Oo0oo0OOO00O0oO0o0O0O> OoO0O0oO00O0o0OOOOoOOooo() {
        return this.OoO0O0oO00O0o0OOOOoOOooo;
    }

    @Override
    public oo0Oo0oo0OOO00O0oO0o0O0O decode(RegistryByteBuf buf) {
        buf.skipBytes(buf.readableBytes());
        return new oo0Oo0oo0OOO00O0oO0o0O0O(this.OoO0O0oO00O0o0OOOOoOOooo);
    }

    public oo0Oo0oo0OOO00O0oO0o0O0O OoO0O0oO00O0o0OOOOoOOooo(RegistryByteBuf buf) {
        return this.decode(buf);
    }

    @Override
    public void encode(RegistryByteBuf buf, oo0Oo0oo0OOO00O0oO0o0O0O payload) {
    }

    public void OoO0O0oO00O0o0OOOOoOOooo(RegistryByteBuf buf, oo0Oo0oo0OOO00O0oO0o0O0O payload) {
        this.encode(buf, payload);
    }

    @Override
    public void receive(oo0Oo0oo0OOO00O0oO0o0O0O payload, ServerPlayNetworking.Context context) {
    }

    public void OoO0O0oO00O0o0OOOOoOOooo(
        oo0Oo0oo0OOO00O0oO0o0O0O payload,
        ServerPlayNetworking.Context context
    ) {
        this.receive(payload, context);
    }

    public interface OoO0O0oO00O0o0OOOOoOOooo<MSG> extends oOo0O00Oooo00OOO0OO0oooo<MSG> {
        default void handle(MSG message, PlayerEntity player, ClientConnection connection) {
        }

        void handleOnServer(MSG message, ServerPlayerEntity player, ClientConnection connection);
    }

    public interface o000ooo0oO0O0ooOO00oo0o0<MSG> {
        void serialize(MSG message, RegistryByteBuf buf);
    }

    public interface oOo0O00Oooo00OOO0OO0oooo<MSG> {
        void handle(MSG message, PlayerEntity player, ClientConnection connection);
    }

    public interface oOoOoO0OoOOoo00ooO0oO00o<MSG> {
        MSG deserialize(RegistryByteBuf buf);
    }

    public record oo0Oo0oo0OOO00O0oO0o0O0O(
        CustomPayload.Id<oo0Oo0oo0OOO00O0oO0o0O0O> OoO0O0oO00O0o0OOOOoOOooo
    ) implements CustomPayload {
        @Override
        public CustomPayload.Id<oo0Oo0oo0OOO00O0oO0o0O0O> getId() {
            return this.OoO0O0oO00O0o0OOOOoOOooo;
        }
    }
}
