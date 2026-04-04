package com.elfmcys.yesstevemodel.mixin.client;

import com.elfmcys.yesstevemodel.Oo0oo0o0OO0oo0OOo0oOOOoo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPacketListenerMixin {
    @Inject(
        method = "onPlayerRespawn(Lnet/minecraft/network/packet/s2c/play/PlayerRespawnS2CPacket;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;init()V",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void ysm$afterRespawn(
        PlayerRespawnS2CPacket packet,
        CallbackInfo ci,
        ClientPlayerEntity previousPlayer,
        ClientPlayerEntity newPlayer
    ) {
        if (previousPlayer != null && newPlayer != null) {
            Oo0oo0o0OO0oo0OOo0oOOOoo.OoO0O0oO00O0o0OOOOoOOooo(previousPlayer, newPlayer);
        }
    }
}
