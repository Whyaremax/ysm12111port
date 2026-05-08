package com.threedmodelnow.compat.ysm;

import com.elfmcys.yesstevemodel.runtime.YsmClientBootstrap;
import com.elfmcys.yesstevemodel.runtime.YsmClientRuntime;
import com.elfmcys.yesstevemodel.runtime.YsmRenderBridge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.threedmodelnow.core.ModelDisplayProvider;
import com.threedmodelnow.core.ModelFormatProvider;
import com.threedmodelnow.core.ModelImportProvider;
import com.threedmodelnow.core.ModelProviderRegistry;
import com.threedmodelnow.core.ThreeDModelNow;
import com.threedmodelnow.forge1211.ModelRenderProvider;
import com.threedmodelnow.forge1211.ModelRenderService;
import com.threedmodelnow.forge1211.ModelBrowserProvider;
import com.threedmodelnow.forge1211.ModelBrowserRegistry;
import com.elfmcys.yesstevemodel.YsmModelScreen;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ThreeDModelNow.YSM_COMPAT_MOD_ID)
public final class YsmCompatAddon {
    private static final String PROVIDER_ID = "ysm";

    public YsmCompatAddon() {
        ModelProviderRegistry.registerFormatProvider(new SimpleFormatProvider());
        ModelProviderRegistry.registerImportProvider(new SimpleImportProvider());
        ModelProviderRegistry.registerDisplayProvider(new SimpleDisplayProvider());
        ModelBrowserRegistry.register(new YsmBrowserProvider());
        ModelRenderService.register(new YsmRenderProvider());
        YsmClientBootstrap.initialize();
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> YsmClientRuntime.tick(Minecraft.getInstance()));
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
        public void beforeWorldRender(DeltaTracker tickCounter) {
            YsmRenderBridge.beforeWorldRender(tickCounter);
        }

        @Override
        public void afterWorldRender() {
            YsmRenderBridge.afterWorldRender();
        }

        @Override
        public boolean renderLocalPlayerBody(
            AvatarRenderState state,
            PoseStack matrices,
            SubmitNodeCollector queue,
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
        public boolean renderLocalArm(PoseStack matrices, HumanoidArm arm) {
            return YsmRenderBridge.renderLocalArm(matrices, arm);
        }

        @Override
        public boolean renderFirstPersonHands(AbstractClientPlayer player, float tickProgress, PoseStack matrices) {
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
        public net.minecraft.client.gui.screens.Screen createScreen() {
            YsmClientRuntime.initialize();
            return new YsmModelScreen();
        }
    }
}
