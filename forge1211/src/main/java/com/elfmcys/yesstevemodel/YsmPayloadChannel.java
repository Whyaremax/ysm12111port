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

public final class YsmPayloadChannel implements StreamCodec<RegistryFriendlyByteBuf, YsmPayloadChannel.EmptyPayload> {
    private final CustomPacketPayload.Type<EmptyPayload> payloadId;

    public YsmPayloadChannel(Identifier channelId) {
        this.payloadId = new CustomPacketPayload.Type<>(Objects.requireNonNull(channelId, "channelId"));
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
        PacketFlow side
    ) {
    }

    public <MSG> void sendToServer(MSG message) {
    }

    public <MSG> void sendToPlayer(ServerPlayer player, MSG message) {
    }

    public <MSG> void sendToPlayers(Iterable<ServerPlayer> players, MSG message) {
    }

    public <MSG> void sendToPlayerTracking(ServerPlayer player, MSG message) {
    }

    public <MSG> void sendToTrackingEntity(Entity entity, MSG message) {
    }

    public <MSG> void broadcastToClients(MSG message) {
    }

    public <MSG> Packet<?> createPacket(MSG message, PacketFlow side) {
        return null;
    }

    public CustomPacketPayload.Type<EmptyPayload> payloadId() {
        return this.payloadId;
    }

    @Override
    public EmptyPayload decode(RegistryFriendlyByteBuf buf) {
        buf.skipBytes(buf.readableBytes());
        return new EmptyPayload(this.payloadId);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, EmptyPayload payload) {
    }

    public interface ServerMessageHandler<MSG> extends ClientMessageHandler<MSG> {
        default void handle(MSG message, Player player, Connection connection) {
        }

        void handleOnServer(MSG message, ServerPlayer player, Connection connection);
    }

    public interface MessageSerializer<MSG> {
        void serialize(MSG message, RegistryFriendlyByteBuf buf);
    }

    public interface ClientMessageHandler<MSG> {
        void handle(MSG message, Player player, Connection connection);
    }

    public interface MessageDeserializer<MSG> {
        MSG deserialize(RegistryFriendlyByteBuf buf);
    }

    public record EmptyPayload(CustomPacketPayload.Type<EmptyPayload> payloadId) implements CustomPacketPayload {
        @Override
        public CustomPacketPayload.Type<EmptyPayload> type() {
            return this.payloadId;
        }
    }
}
