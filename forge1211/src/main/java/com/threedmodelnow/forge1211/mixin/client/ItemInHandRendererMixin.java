package com.threedmodelnow.forge1211.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.threedmodelnow.forge1211.ModelRenderService;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void threedmodelnow$renderFirstPersonItem(
        AbstractClientPlayer player,
        float tickProgress,
        float pitch,
        InteractionHand hand,
        float swingProgress,
        ItemStack item,
        float equipProgress,
        PoseStack matrices,
        SubmitNodeCollector queue,
        int light,
        CallbackInfo ci
    ) {
        if (ModelRenderService.renderFirstPersonHands(player, tickProgress, matrices)) {
            ci.cancel();
        }
    }
}
