package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.OooooOoo0000O00ooOOOoo0o;
import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.oOOOoo00ooOO00OOoO0OoOoO;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.vehicle.BoatEntity;

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
                OooooOoo0000O00ooOOOoo0o.OoO0O0oO00O0o0OOOOoOOooo();
            }
        } catch (Throwable throwable) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("Failed to initialize YSM first-person compat", throwable);
        }

        try {
            if (isLoaded("playeranimator")) {
                oOOOoo00ooOO00OOoO0OoOoO.OoO0O0oO00O0o0OOOOoOOooo();
            }
        } catch (Throwable throwable) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("Failed to initialize YSM PlayerAnimator compat", throwable);
        }

        compatibilityInitialized = true;
    }

    public static void beforeWorldRender(RenderTickCounter tickCounter) {
        initializeCompatibility();
    }

    public static void afterWorldRender() {
    }

    public static boolean renderLocalPlayerBody(
        PlayerEntityRenderState state,
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        CameraRenderState cameraRenderState
    ) {
        initializeCompatibility();

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        YsmGeoPlayerRenderer renderer = YsmRenderStateStore.activeRenderer();
        if (!YesSteveModel.isAvailable() || renderer == null || player == null || client.world == null || state.id != player.getId()) {
            return false;
        }

        float tickProgress = client.getRenderTickCounter().getTickProgress(true);
        int light = WorldRenderer.getLightmapCoordinates(player.getEntityWorld(), player.getBlockPos());
        renderer.renderBody(new YsmRenderContext(player, state, capturePose(player, state)), matrices, queue, cameraRenderState, light, tickProgress);
        return true;
    }

    public static void beginInventoryPreview(LivingEntity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
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

    public static boolean renderLocalArm(MatrixStack matrices, Arm arm) {
        return YsmFirstPersonCompat.shouldUseFirstPersonModel() && YsmRenderStateStore.hasActivePack();
    }

    public static boolean renderFirstPersonHands(AbstractClientPlayerEntity player, float tickProgress, MatrixStack matrices) {
        return YsmFirstPersonCompat.shouldUseFirstPersonModel() && YsmRenderStateStore.hasActivePack();
    }

    private static YsmPoseSnapshot capturePose(ClientPlayerEntity player, PlayerEntityRenderState state) {
        boolean swimming = state.pose == EntityPose.SWIMMING || ((BipedEntityRenderState) state).isSwimming;
        boolean gliding = state.pose == EntityPose.GLIDING || ((BipedEntityRenderState) state).isGliding;
        boolean flying = player.getAbilities().flying && !gliding;
        boolean sneaking = state.sneaking || ((BipedEntityRenderState) state).isInSneakingPose;
        boolean moving = state.limbSwingAmplitude > 0.02f || player.getVelocity().horizontalLengthSquared() > 0.001;
        boolean riding = player.hasVehicle();
        boolean boat = player.getVehicle() instanceof BoatEntity;
        boolean pigRide = player.getVehicle() instanceof PigEntity;
        boolean climbing = player.isClimbing();
        boolean climb = climbing && moving;
        float inputVertical = 0.0f;
        float inputHorizontal = 0.0f;
        if (player.input != null) {
            inputVertical = (player.input.playerInput.forward() ? 1.0f : 0.0f) - (player.input.playerInput.backward() ? 1.0f : 0.0f);
            inputHorizontal = (player.input.playerInput.right() ? 1.0f : 0.0f) - (player.input.playerInput.left() ? 1.0f : 0.0f);
        }

        return new YsmPoseSnapshot(
            state.bodyYaw,
            player.getHeadYaw(),
            state.relativeHeadYaw,
            state.pitch,
            state.limbSwingAmplitude,
            (float) player.getVelocity().y,
            inputVertical,
            inputHorizontal,
            state.flyingRotation,
            moving || Math.abs(inputVertical) > 0.01f || Math.abs(inputHorizontal) > 0.01f,
            player.isSprinting(),
            sneaking,
            sneaking && !moving,
            !player.isOnGround() && !flying && !gliding && !swimming && player.getVelocity().y > 0.08,
            flying,
            gliding,
            state.usingRiptide,
            swimming,
            state.touchingWater && !swimming,
            riding,
            boat,
            pigRide,
            climb,
            climbing,
            state.pose == EntityPose.SLEEPING,
            state.deathTime > 0.0f,
            !player.getStackInHand(Hand.MAIN_HAND).isEmpty(),
            !player.getStackInHand(Hand.OFF_HAND).isEmpty(),
            player.getHungerManager().getFoodLevel()
        );
    }

    private static boolean isLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean hasRuntimeBackedSelection() {
        return YsmRenderStateStore.hasActivePack();
    }

    public static boolean hasLiveModel() {
        return YsmRenderStateStore.hasActivePack();
    }
}
