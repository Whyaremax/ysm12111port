package com.threedmodelnow.forge1211;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public interface ModelRenderProvider {
    default void beforeWorldRender(DeltaTracker tickCounter) {
    }

    default void afterWorldRender() {
    }

    default boolean renderLocalPlayerBody(
        AvatarRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraRenderState
    ) {
        return false;
    }

    default void beginInventoryPreview(LivingEntity entity) {
    }

    default void endInventoryPreview() {
    }

    default boolean renderLocalArm(PoseStack matrices, HumanoidArm arm) {
        return false;
    }

    default boolean renderFirstPersonHands(AbstractClientPlayer player, float tickProgress, PoseStack matrices) {
        return false;
    }
}
