package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YesSteveModel;
import java.util.Objects;

public final class YsmBootstrapSupport {
    private static volatile boolean commonInitialized;
    private static volatile boolean clientInitialized;
    private static volatile boolean runtimeAvailable = true;
    private static volatile String statusMessage = "YSM Java runtime active";

    private YsmBootstrapSupport() {
    }

    public static boolean isAvailable() {
        return runtimeAvailable;
    }

    public static String statusMessage() {
        return statusMessage;
    }

    public static void initializeCommon() {
        if (commonInitialized) {
            return;
        }

        synchronized (YsmBootstrapSupport.class) {
            if (commonInitialized) {
                return;
            }

            runInit("payload definitions", com.elfmcys.yesstevemodel.OO000oo00oo0O0O0oO0OoOoo::oo0Oo0oo0OOO00O0oO0o0O0O);
            runInit("common scaffolding", YsmCommonBootstrap::registerHooks);

            commonInitialized = true;
        }
    }

    public static void initializeClient() {
        clientInitialized = true;
    }

    public static void markUnavailable(String message, Throwable throwable) {
        runtimeAvailable = false;
        statusMessage = Objects.requireNonNullElse(message, "YSM Java runtime unavailable");
        if (throwable == null) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error(statusMessage);
        } else {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error(statusMessage, throwable);
        }
    }

    public static void setStatusMessage(String message) {
        statusMessage = Objects.requireNonNullElse(message, statusMessage);
    }

    public static void runInit(String label, Runnable action) {
        try {
            action.run();
        } catch (Throwable throwable) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("Skipped YSM {} on 1.21.11", label, throwable);
        }
    }
}
