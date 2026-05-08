package com.threedmodelnow.fabric1211.mixin.client;

import com.threedmodelnow.fabric1211.ModelRenderService;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V", at = @At("HEAD"))
    private static void threedmodelnow$beginInventoryPreview(
        DrawContext context,
        int x1,
        int y1,
        int x2,
        int y2,
        int size,
        float extraYOffset,
        float mouseX,
        float mouseY,
        LivingEntity entity,
        CallbackInfo ci
    ) {
        ModelRenderService.beginInventoryPreview(entity);
    }

    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V", at = @At("RETURN"))
    private static void threedmodelnow$endInventoryPreview(
        DrawContext context,
        int x1,
        int y1,
        int x2,
        int y2,
        int size,
        float extraYOffset,
        float mouseX,
        float mouseY,
        LivingEntity entity,
        CallbackInfo ci
    ) {
        ModelRenderService.endInventoryPreview();
    }
}
