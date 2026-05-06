package com.threedmodelnow.forge1211.mixin.client;

import com.threedmodelnow.forge1211.ModelRenderService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @Inject(method = "renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"))
    private static void threedmodelnow$beginInventoryPreview(
        GuiGraphics context,
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

    @Inject(method = "renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V", at = @At("RETURN"))
    private static void threedmodelnow$endInventoryPreview(
        GuiGraphics context,
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
