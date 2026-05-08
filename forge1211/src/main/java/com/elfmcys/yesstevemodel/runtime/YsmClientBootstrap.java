package com.elfmcys.yesstevemodel.runtime;

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

        runtimeInitialized = true;
    }
}
