package com.threedmodelnow.forge1211.mixin.client;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.threedmodelnow.forge1211.ModelRenderService;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void threedmodelnow$beforeWorldRender(
        GraphicsResourceAllocator allocator,
        DeltaTracker tickCounter,
        boolean renderBlockOutline,
        Camera camera,
        Matrix4f positionMatrix,
        Matrix4f projectionMatrix,
        Matrix4f frustumMatrix,
        GpuBufferSlice fogBuffer,
        Vector4f clearColor,
        boolean shouldRenderClouds,
        CallbackInfo ci
    ) {
        ModelRenderService.beforeWorldRender(tickCounter);
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void threedmodelnow$afterWorldRender(
        GraphicsResourceAllocator allocator,
        DeltaTracker tickCounter,
        boolean renderBlockOutline,
        Camera camera,
        Matrix4f positionMatrix,
        Matrix4f projectionMatrix,
        Matrix4f frustumMatrix,
        GpuBufferSlice fogBuffer,
        Vector4f clearColor,
        boolean shouldRenderClouds,
        CallbackInfo ci
    ) {
        ModelRenderService.afterWorldRender();
    }
}
