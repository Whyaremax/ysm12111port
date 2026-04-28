package com.elfmcys.yesstevemodel;

import io.netty.channel.Channel;
import java.util.Objects;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class YsmPayloadChannel implements
    ServerPlayNetworking.PlayPayloadHandler<YsmPayloadChannel.EmptyPayload>,
    PacketCodec<RegistryByteBuf, YsmPayloadChannel.EmptyPayload> {
    private final CustomPayload.Id<EmptyPayload> payloadId;

    public YsmPayloadChannel(Identifier channelId) {
        this.payloadId = new CustomPayload.Id<>(Objects.requireNonNull(channelId, "channelId"));
    }

    public <MSG> void registerServerMessage(
        int id,
        Class<MSG> messageType,
        MessageSerializer<MSG> serializer,
        MessageDeserializer<MSG> deserializer,
        ServerMessageHandler<MSG> handler
    ) {
    }

    public <MSG> void registerClientMessage(
        int id,
        Class<MSG> messageType,
        MessageSerializer<MSG> serializer,
        MessageDeserializer<MSG> deserializer,
        ClientMessageHandler<MSG> handler
    ) {
    }

    public <MSG> void registerMessage(
        int id,
        Class<MSG> messageType,
        MessageSerializer<MSG> serializer,
        MessageDeserializer<MSG> deserializer,
        ClientMessageHandler<MSG> handler,
        NetworkSide side
    ) {
    }

    public <MSG> void sendToServer(MSG message) {
    }

    public <MSG> void sendToPlayer(ServerPlayerEntity player, MSG message) {
    }

    public <MSG> void sendToPlayers(Iterable<ServerPlayerEntity> players, MSG message) {
    }

    public <MSG> void sendToPlayerTracking(ServerPlayerEntity player, MSG message) {
    }

    public <MSG> void sendToTrackingEntity(Entity entity, MSG message) {
    }

    public <MSG> void broadcastToClients(MSG message) {
    }

    public <MSG> Packet<?> createPacket(MSG message, NetworkSide side) {
        EmptyPayload payload = new EmptyPayload(this.payloadId);
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

    public CustomPayload.Id<EmptyPayload> payloadId() {
        return this.payloadId;
    }

    @Override
    public EmptyPayload decode(RegistryByteBuf buf) {
        buf.skipBytes(buf.readableBytes());
        return new EmptyPayload(this.payloadId);
    }

    @Override
    public void encode(RegistryByteBuf buf, EmptyPayload payload) {
    }

    @Override
    public void receive(EmptyPayload payload, ServerPlayNetworking.Context context) {
    }

    public interface ServerMessageHandler<MSG> extends ClientMessageHandler<MSG> {
        default void handle(MSG message, PlayerEntity player, ClientConnection connection) {
        }

        void handleOnServer(MSG message, ServerPlayerEntity player, ClientConnection connection);
    }

    public interface MessageSerializer<MSG> {
        void serialize(MSG message, RegistryByteBuf buf);
    }

    public interface ClientMessageHandler<MSG> {
        void handle(MSG message, PlayerEntity player, ClientConnection connection);
    }

    public interface MessageDeserializer<MSG> {
        MSG deserialize(RegistryByteBuf buf);
    }

    public record EmptyPayload(CustomPayload.Id<EmptyPayload> payloadId) implements CustomPayload {
        @Override
        public CustomPayload.Id<EmptyPayload> getId() {
            return this.payloadId;
        }
    }
}
