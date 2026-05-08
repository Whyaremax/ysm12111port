package com.threedmodelnow.fabric1211;

import com.threedmodelnow.core.ThreeDModelNow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public final class ThreeDModelNowMod implements ClientModInitializer, ModInitializer {
    @Override
    public void onInitialize() {
        ThreeDModelNow.LOGGER.info("3DModelNow core initialized");
    }

    @Override
    public void onInitializeClient() {
        ModelSelectionKeyBindings.register();
        ThreeDModelNow.LOGGER.info("3DModelNow Fabric 1.21.11 client initialized");
    }
}
