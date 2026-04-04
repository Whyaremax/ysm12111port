package com.elfmcys.yesstevemodel.mixin.client;

import com.elfmcys.yesstevemodel.runtime.YsmRenderBridge;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin {
    @Inject(method = "renderRightArm", at = @At("HEAD"), cancellable = true)
    private void ysm$renderRightArm(
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        int light,
        Identifier skinTexture,
        boolean sleeveVisible,
        CallbackInfo ci
    ) {
        if (YsmRenderBridge.renderLocalArm(matrices, Arm.RIGHT)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftArm", at = @At("HEAD"), cancellable = true)
    private void ysm$renderLeftArm(
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        int light,
        Identifier skinTexture,
        boolean sleeveVisible,
        CallbackInfo ci
    ) {
        if (YsmRenderBridge.renderLocalArm(matrices, Arm.LEFT)) {
            ci.cancel();
        }
    }
}
