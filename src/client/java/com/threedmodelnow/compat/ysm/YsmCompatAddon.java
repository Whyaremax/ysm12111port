package com.threedmodelnow.compat.ysm;

import com.elfmcys.yesstevemodel.runtime.YsmClientBootstrap;
import com.elfmcys.yesstevemodel.runtime.YsmClientRuntime;
import com.elfmcys.yesstevemodel.runtime.YsmRenderBridge;
import com.threedmodelnow.core.ModelDisplayProvider;
import com.threedmodelnow.core.ModelFormatProvider;
import com.threedmodelnow.core.ModelImportProvider;
import com.threedmodelnow.core.ModelProviderRegistry;
import com.threedmodelnow.core.ThreeDModelNow;
import com.threedmodelnow.fabric1211.ModelRenderProvider;
import com.threedmodelnow.fabric1211.ModelRenderService;
import com.threedmodelnow.fabric1211.ModelBrowserProvider;
import com.threedmodelnow.fabric1211.ModelBrowserRegistry;
import com.elfmcys.yesstevemodel.YsmModelScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

public final class YsmCompatAddon implements ClientModInitializer {
    private static final String PROVIDER_ID = "ysm";

    @Override
    public void onInitializeClient() {
        ModelProviderRegistry.registerFormatProvider(new SimpleFormatProvider());
        ModelProviderRegistry.registerImportProvider(new SimpleImportProvider());
        ModelProviderRegistry.registerDisplayProvider(new SimpleDisplayProvider());
        ModelBrowserRegistry.register(new YsmBrowserProvider());
        ModelRenderService.register(new YsmRenderProvider());
        YsmClientBootstrap.initialize();
        ThreeDModelNow.LOGGER.info("3DModelNow YSM compatibility addon initialized");
    }

    private record SimpleFormatProvider() implements ModelFormatProvider {
        @Override
        public String id() {
            return PROVIDER_ID;
        }

        @Override
        public String displayName() {
            return "YSM model packs";
        }
    }

    private record SimpleImportProvider() implements ModelImportProvider {
        @Override
        public String id() {
            return PROVIDER_ID;
        }

        @Override
        public String displayName() {
            return "YSM / OpenYSM import";
        }
    }

    private record SimpleDisplayProvider() implements ModelDisplayProvider {
        @Override
        public String id() {
            return PROVIDER_ID;
        }

        @Override
        public String displayName() {
            return "YSM display adapter";
        }
    }

    private static final class YsmRenderProvider implements ModelRenderProvider {
        @Override
        public void beforeWorldRender(RenderTickCounter tickCounter) {
            YsmRenderBridge.beforeWorldRender(tickCounter);
        }

        @Override
        public void afterWorldRender() {
            YsmRenderBridge.afterWorldRender();
        }

        @Override
        public boolean renderLocalPlayerBody(
            PlayerEntityRenderState state,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            CameraRenderState cameraRenderState
        ) {
            return YsmRenderBridge.renderLocalPlayerBody(state, matrices, queue, cameraRenderState);
        }

        @Override
        public void beginInventoryPreview(LivingEntity entity) {
            YsmRenderBridge.beginInventoryPreview(entity);
        }

        @Override
        public void endInventoryPreview() {
            YsmRenderBridge.endInventoryPreview();
        }

        @Override
        public boolean renderLocalArm(MatrixStack matrices, Arm arm) {
            return YsmRenderBridge.renderLocalArm(matrices, arm);
        }

        @Override
        public boolean renderFirstPersonHands(AbstractClientPlayerEntity player, float tickProgress, MatrixStack matrices) {
            YsmClientRuntime.initialize();
            return YsmRenderBridge.renderFirstPersonHands(player, tickProgress, matrices);
        }
    }

    private record YsmBrowserProvider() implements ModelBrowserProvider {
        @Override
        public String id() {
            return PROVIDER_ID;
        }

        @Override
        public String displayName() {
            return "YSM Packs";
        }

        @Override
        public net.minecraft.client.gui.screen.Screen createScreen() {
            YsmClientRuntime.initialize();
            return new YsmModelScreen();
        }
    }
}
