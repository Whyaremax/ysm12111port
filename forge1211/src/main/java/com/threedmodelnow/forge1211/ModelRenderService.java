package com.threedmodelnow.forge1211;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public final class ModelRenderService {
    private static final List<ModelRenderProvider> PROVIDERS = new ArrayList<>();

    private ModelRenderService() {
    }

    public static synchronized void register(ModelRenderProvider provider) {
        PROVIDERS.add(provider);
    }

    public static synchronized boolean hasProviders() {
        return !PROVIDERS.isEmpty();
    }

    public static void beforeWorldRender(DeltaTracker tickCounter) {
        for (ModelRenderProvider provider : snapshot()) {
            provider.beforeWorldRender(tickCounter);
        }
    }

    public static void afterWorldRender() {
        for (ModelRenderProvider provider : snapshot()) {
            provider.afterWorldRender();
        }
    }

    public static boolean renderLocalPlayerBody(
        AvatarRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraRenderState
    ) {
        for (ModelRenderProvider provider : snapshot()) {
            if (provider.renderLocalPlayerBody(state, matrices, queue, cameraRenderState)) {
                return true;
            }
        }
        return false;
    }

    public static void beginInventoryPreview(LivingEntity entity) {
        for (ModelRenderProvider provider : snapshot()) {
            provider.beginInventoryPreview(entity);
        }
    }

    public static void endInventoryPreview() {
        for (ModelRenderProvider provider : snapshot()) {
            provider.endInventoryPreview();
        }
    }

    public static boolean renderLocalArm(PoseStack matrices, HumanoidArm arm) {
        for (ModelRenderProvider provider : snapshot()) {
            if (provider.renderLocalArm(matrices, arm)) {
                return true;
            }
        }
        return false;
    }

    public static boolean renderFirstPersonHands(AbstractClientPlayer player, float tickProgress, PoseStack matrices) {
        for (ModelRenderProvider provider : snapshot()) {
            if (provider.renderFirstPersonHands(player, tickProgress, matrices)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized List<ModelRenderProvider> snapshot() {
        return List.copyOf(PROVIDERS);
    }
}
