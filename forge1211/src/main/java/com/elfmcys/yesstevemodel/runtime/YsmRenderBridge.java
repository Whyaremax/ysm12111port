package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.threedmodelnow.forge1211.ForgePlatform;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.vehicle.boat.Boat;

public final class YsmRenderBridge {
    private static boolean compatibilityInitialized;
    private static final ThreadLocal<Boolean> INVENTORY_PREVIEW_RENDERING = ThreadLocal.withInitial(() -> false);

    private YsmRenderBridge() {
    }

    public static void initializeCompatibility() {
        if (compatibilityInitialized) {
            return;
        }

        YsmFirstPersonCompat.initialize();

        try {
            if (isLoaded("firstperson") || isLoaded("firstpersonmod")) {
                YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.debug("First-person compatibility mod detected on Forge");
            }
        } catch (Throwable throwable) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("Failed to initialize YSM first-person compat", throwable);
        }

        try {
            if (isLoaded("playeranimator")) {
                YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.debug("PlayerAnimator compatibility mod detected on Forge");
            }
        } catch (Throwable throwable) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("Failed to initialize YSM PlayerAnimator compat", throwable);
        }

        compatibilityInitialized = true;
    }

    public static void beforeWorldRender(DeltaTracker tickCounter) {
        initializeCompatibility();
    }

    public static void afterWorldRender() {
    }

    public static boolean renderLocalPlayerBody(
        AvatarRenderState state,
        PoseStack matrices,
        SubmitNodeCollector queue,
        CameraRenderState cameraRenderState
    ) {
        initializeCompatibility();

        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        YsmGeoPlayerRenderer renderer = YsmRenderStateStore.activeRenderer();
        if (!YesSteveModel.isAvailable() || renderer == null || player == null || client.level == null || state.id != player.getId()) {
            return false;
        }

        float tickProgress = client.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        int light = LevelRenderer.getLightColor(player.level(), player.blockPosition());
        renderer.renderBody(new YsmRenderContext(player, state, capturePose(player, state)), matrices, queue, cameraRenderState, light, tickProgress);
        return true;
    }

    public static void beginInventoryPreview(LivingEntity entity) {
        Minecraft client = Minecraft.getInstance();
        if (client != null && entity == client.player && YsmRenderStateStore.hasActivePack()) {
            INVENTORY_PREVIEW_RENDERING.set(true);
        }
    }

    public static void endInventoryPreview() {
        INVENTORY_PREVIEW_RENDERING.set(false);
    }

    public static boolean isInventoryPreviewRendering() {
        return INVENTORY_PREVIEW_RENDERING.get();
    }

    public static boolean renderLocalArm(PoseStack matrices, HumanoidArm arm) {
        return YsmFirstPersonCompat.shouldUseFirstPersonModel() && YsmRenderStateStore.hasActivePack();
    }

    public static boolean renderFirstPersonHands(AbstractClientPlayer player, float tickProgress, PoseStack matrices) {
        return YsmFirstPersonCompat.shouldUseFirstPersonModel() && YsmRenderStateStore.hasActivePack();
    }

    private static YsmPoseSnapshot capturePose(LocalPlayer player, AvatarRenderState state) {
        boolean swimming = state.pose == Pose.SWIMMING || ((HumanoidRenderState) state).isVisuallySwimming;
        boolean gliding = state.pose == Pose.FALL_FLYING || ((HumanoidRenderState) state).isFallFlying;
        boolean flying = player.getAbilities().flying && !gliding;
        boolean sneaking = state.isDiscrete || ((HumanoidRenderState) state).isCrouching;
        boolean moving = state.walkAnimationSpeed > 0.02f || player.getDeltaMovement().horizontalDistanceSqr() > 0.001;
        boolean riding = player.isPassenger();
        boolean boat = player.getVehicle() instanceof Boat;
        boolean pigRide = player.getVehicle() instanceof Pig;
        boolean climbing = player.onClimbable();
        boolean climb = climbing && moving;
        float inputVertical = 0.0f;
        float inputHorizontal = 0.0f;
        if (player.input != null) {
            inputVertical = (player.input.keyPresses.forward() ? 1.0f : 0.0f) - (player.input.keyPresses.backward() ? 1.0f : 0.0f);
            inputHorizontal = (player.input.keyPresses.right() ? 1.0f : 0.0f) - (player.input.keyPresses.left() ? 1.0f : 0.0f);
        }

        return new YsmPoseSnapshot(
            state.bodyRot,
            player.getYHeadRot(),
            state.yRot,
            state.xRot,
            state.walkAnimationSpeed,
            (float) player.getDeltaMovement().y,
            inputVertical,
            inputHorizontal,
            state.flyingYRot,
            moving || Math.abs(inputVertical) > 0.01f || Math.abs(inputHorizontal) > 0.01f,
            player.isSprinting(),
            sneaking,
            sneaking && !moving,
            !player.onGround() && !flying && !gliding && !swimming && player.getDeltaMovement().y > 0.08,
            flying,
            gliding,
            state.isAutoSpinAttack,
            swimming,
            state.isInWater && !swimming,
            riding,
            boat,
            pigRide,
            climb,
            climbing,
            state.pose == Pose.SLEEPING,
            state.deathTime > 0.0f,
            !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty(),
            !player.getItemInHand(InteractionHand.OFF_HAND).isEmpty(),
            player.getFoodData().getFoodLevel()
        );
    }

    private static boolean isLoaded(String modId) {
        return ForgePlatform.isModLoaded(modId);
    }

    public static boolean hasRuntimeBackedSelection() {
        return YsmRenderStateStore.hasActivePack();
    }

    public static boolean hasLiveModel() {
        return YsmRenderStateStore.hasActivePack();
    }
}
