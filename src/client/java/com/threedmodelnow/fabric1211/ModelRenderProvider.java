package com.threedmodelnow.fabric1211;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

public interface ModelRenderProvider {
    default void beforeWorldRender(RenderTickCounter tickCounter) {
    }

    default void afterWorldRender() {
    }

    default boolean renderLocalPlayerBody(
        PlayerEntityRenderState state,
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        CameraRenderState cameraRenderState
    ) {
        return false;
    }

    default void beginInventoryPreview(LivingEntity entity) {
    }

    default void endInventoryPreview() {
    }

    default boolean renderLocalArm(MatrixStack matrices, Arm arm) {
        return false;
    }

    default boolean renderFirstPersonHands(AbstractClientPlayerEntity player, float tickProgress, MatrixStack matrices) {
        return false;
    }
}
