package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YsmOpenModelScreenKeyBinding;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public final class YsmClientBootstrap {
    private static boolean runtimeInitialized;

    private YsmClientBootstrap() {
    }

    public static void initialize() {
        if (runtimeInitialized) {
            return;
        }

        YsmBootstrapSupport.initializeCommon();
        YsmBootstrapSupport.initializeClient();
        YsmBootstrapSupport.runInit("client runtime", YsmClientRuntime::initialize);
        YsmBootstrapSupport.runInit("render compat", YsmRenderBridge::initializeCompatibility);
        YsmBootstrapSupport.runInit(
            "key bindings",
            () -> KeyBindingHelper.registerKeyBinding(YsmOpenModelScreenKeyBinding.OPEN_MODEL_SCREEN)
        );
        YsmBootstrapSupport.runInit("selection reapply", () -> ClientTickEvents.END_CLIENT_TICK.register(YsmClientRuntime::tick));

        runtimeInitialized = true;
    }
}
