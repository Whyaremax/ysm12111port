package com.elfmcys.yesstevemodel.runtime;

public final class YsmCommonBootstrap {
    private static boolean hooksRegistered;

    private YsmCommonBootstrap() {
    }

    public static void registerHooks() {
        if (hooksRegistered) {
            return;
        }

        // The current 1.21.11 port is client-only. Keep the common bootstrap as
        // scaffolding only and do not register legacy server hooks.
        hooksRegistered = true;
    }
}
