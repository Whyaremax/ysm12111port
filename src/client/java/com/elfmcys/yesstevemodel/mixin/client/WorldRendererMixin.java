package com.elfmcys.yesstevemodel.mixin.client;

import com.elfmcys.yesstevemodel.runtime.YsmRenderBridge;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void ysm$beforeWorldRender(
        ObjectAllocator allocator,
        RenderTickCounter tickCounter,
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
        YsmRenderBridge.beforeWorldRender(tickCounter);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void ysm$afterWorldRender(
        ObjectAllocator allocator,
        RenderTickCounter tickCounter,
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
        YsmRenderBridge.afterWorldRender();
    }
}
