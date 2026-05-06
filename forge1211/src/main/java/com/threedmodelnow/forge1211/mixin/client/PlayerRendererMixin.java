package com.threedmodelnow.forge1211.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.threedmodelnow.forge1211.ModelRenderService;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin {
    @Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
    private void threedmodelnow$renderRightArm(
        PoseStack matrices,
        SubmitNodeCollector queue,
        int light,
        Identifier skinTexture,
        boolean sleeveVisible,
        CallbackInfo ci
    ) {
        if (ModelRenderService.renderLocalArm(matrices, HumanoidArm.RIGHT)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    private void threedmodelnow$renderLeftArm(
        PoseStack matrices,
        SubmitNodeCollector queue,
        int light,
        Identifier skinTexture,
        boolean sleeveVisible,
        CallbackInfo ci
    ) {
        if (ModelRenderService.renderLocalArm(matrices, HumanoidArm.LEFT)) {
            ci.cancel();
        }
    }
}
