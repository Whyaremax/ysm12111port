package com.threedmodelnow.fabric1211;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

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

    public static void beforeWorldRender(RenderTickCounter tickCounter) {
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
        PlayerEntityRenderState state,
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
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

    public static boolean renderLocalArm(MatrixStack matrices, Arm arm) {
        for (ModelRenderProvider provider : snapshot()) {
            if (provider.renderLocalArm(matrices, arm)) {
                return true;
            }
        }
        return false;
    }

    public static boolean renderFirstPersonHands(AbstractClientPlayerEntity player, float tickProgress, MatrixStack matrices) {
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
