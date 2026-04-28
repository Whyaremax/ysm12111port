package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmBootstrapSupport;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YesSteveModel implements ClientModInitializer, ModInitializer {
    public static final String MOD_ID = "yes_steve_model";
    public static String RUNTIME_KIND = "java-runtime";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Deprecated
    public static final String OoO0O0oO00O0o0OOOOoOOooo = MOD_ID;

    @Deprecated
    public static String oo0Oo0oo0OOO00O0oO0o0O0O = RUNTIME_KIND;

    @Deprecated
    public static final Logger oOoOoO0OoOOoo00ooO0oO00o = LOGGER;

    @Override
    public void onInitialize() {
        try {
            OOoo0oooo0O0OO0oo00oOOO0.OoO0O0oO00O0o0OOOOoOOooo();
            O00o0OoO0ooO0oO0OO0Oo0Oo.OoO0O0oO00O0o0OOOOoOOooo(true);
        } catch (Throwable throwable) {
            YsmBootstrapSupport.markUnavailable("Failed to initialize YSM Java runtime", throwable);
        }
    }

    @Override
    public void onInitializeClient() {
        try {
            Class.forName("com.elfmcys.yesstevemodel.runtime.YsmClientBootstrap")
                .getMethod("initialize")
                .invoke(null);
        } catch (Throwable throwable) {
            YsmBootstrapSupport.markUnavailable("Failed to initialize YSM client runtime", throwable);
        }
    }

    @oO00O0o0oOo0o0o0OO0oOooO
    public static boolean isAvailable() {
        return YsmBootstrapSupport.isAvailable();
    }

    public static boolean isRuntimeAvailable() {
        return YsmBootstrapSupport.isAvailable();
    }

    @Deprecated
    public static boolean OoO0O0oO00O0o0OOOOoOOooo() {
        return isRuntimeAvailable();
    }

    public static void sendUnavailableStatusToClientPlayer() {
        try {
            Class<?> minecraftClientClass = Class.forName("net.minecraft.client.MinecraftClient");
            Object client = minecraftClientClass.getMethod("getInstance").invoke(null);
            Object player = minecraftClientClass.getField("player").get(client);
            if (player != null) {
                player.getClass()
                    .getMethod("sendMessage", Text.class, boolean.class)
                    .invoke(player, runtimeStatusText(), false);
            }
        } catch (Throwable throwable) {
            LOGGER.debug("Unable to deliver client-side status message", throwable);
        }
    }

    @Deprecated
    public static void oo0Oo0oo0OOO00O0oO0o0O0O() {
        sendUnavailableStatusToClientPlayer();
    }

    public static Text runtimeStatusText() {
        return OOoo0oooo0O0OO0oo00oOOO0.o0o0O0OOo0O0O0oO00oooO00();
    }

    @Deprecated
    public static Text oOoOoO0OoOOoo00ooO0oO00o() {
        return runtimeStatusText();
    }

    public static String runtimeStatusMessage() {
        return OOoo0oooo0O0OO0oo00oOOO0.oOo0O00Oooo00OOO0OO0oooo();
    }

    @Deprecated
    public static String o0o0O0OOo0O0O0oO00oooO00() {
        return runtimeStatusMessage();
    }
}
