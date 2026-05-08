package com.elfmcys.yesstevemodel.mixin.client;

import com.elfmcys.yesstevemodel.runtime.YsmRenderBridge;
import com.mojang.blaze3d.vertex.PoseStack;
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
    private void ysm$renderRightArm(
        PoseStack matrices,
        SubmitNodeCollector queue,
        int light,
        Identifier skinTexture,
        boolean sleeveVisible,
        CallbackInfo ci
    ) {
        if (YsmRenderBridge.renderLocalArm(matrices, HumanoidArm.RIGHT)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    private void ysm$renderLeftArm(
        PoseStack matrices,
        SubmitNodeCollector queue,
        int light,
        Identifier skinTexture,
        boolean sleeveVisible,
        CallbackInfo ci
    ) {
        if (YsmRenderBridge.renderLocalArm(matrices, HumanoidArm.LEFT)) {
            ci.cancel();
        }
    }
}
