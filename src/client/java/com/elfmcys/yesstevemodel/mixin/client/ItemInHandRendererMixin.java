package com.elfmcys.yesstevemodel.mixin.client;

import com.elfmcys.yesstevemodel.runtime.YsmRenderBridge;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(
        method = "renderFirstPersonItem",
        at = @At("HEAD"),
        cancellable = true
    )
    private void ysm$renderFirstPersonItem(
        AbstractClientPlayerEntity player,
        float tickProgress,
        float pitch,
        Hand hand,
        float swingProgress,
        ItemStack item,
        float equipProgress,
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        int light,
        CallbackInfo ci
    ) {
        if (YsmRenderBridge.renderFirstPersonHands(player, tickProgress, matrices)) {
            ci.cancel();
        }
    }
}
