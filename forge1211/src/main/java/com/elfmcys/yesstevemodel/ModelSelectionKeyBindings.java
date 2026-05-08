package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmClientRuntime;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class ModelSelectionKeyBindings {
    public static final KeyMapping OPEN_MODEL_SELECTION =
        com.threedmodelnow.forge1211.ModelSelectionKeyBindings.OPEN_MODEL_SELECTION;

    private ModelSelectionKeyBindings() {
    }

    public static void register() {
    }

    private static void openModelSelection(Minecraft client) {
        if (client == null) {
            return;
        }

        if (!YesSteveModel.isAvailable()) {
            YesSteveModel.oo0Oo0oo0OOO00O0oO0o0O0O();
            return;
        }

        YsmClientRuntime.initialize();
        client.setScreen(new YsmModelScreen());
    }
}
