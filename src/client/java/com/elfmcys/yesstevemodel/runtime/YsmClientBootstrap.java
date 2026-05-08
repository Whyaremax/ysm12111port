package com.elfmcys.yesstevemodel.runtime;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

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
        YsmBootstrapSupport.runInit("probe commands", YsmProbeCommand::register);
        YsmBootstrapSupport.runInit("selection reapply", () -> ClientTickEvents.END_CLIENT_TICK.register(YsmClientRuntime::tick));

        runtimeInitialized = true;
    }
}
