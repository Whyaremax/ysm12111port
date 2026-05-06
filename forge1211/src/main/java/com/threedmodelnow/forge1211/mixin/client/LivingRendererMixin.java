package com.threedmodelnow.forge1211.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.threedmodelnow.forge1211.ModelRenderService;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingRendererMixin {
    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void threedmodelnow$renderSelectedLocalPlayer(
        LivingEntityRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraRenderState,
        CallbackInfo ci
    ) {
        if (state instanceof AvatarRenderState playerState && ModelRenderService.renderLocalPlayerBody(playerState, matrices, queue, cameraRenderState)) {
            ci.cancel();
        }
    }
}
