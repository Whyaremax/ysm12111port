package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmBootstrapSupport;
import java.io.IOException;
import net.minecraft.network.chat.Component;

public final class OOoo0oooo0O0OO0oo00oOOO0 {
    private OOoo0oooo0O0OO0oo00oOOO0() {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo() throws IOException {
        YsmBootstrapSupport.setStatusMessage("YSM Java runtime active");
    }

    public static boolean oo0Oo0oo0OOO00O0oO0o0O0O() {
        return YsmBootstrapSupport.isAvailable();
    }

    public static boolean oOoOoO0OoOOoo00ooO0oO00o() {
        return YsmBootstrapSupport.isAvailable();
    }

    public static Component o0o0O0OOo0O0O0oO00oooO00() {
        return Component.literal(YsmBootstrapSupport.statusMessage());
    }

    public static String oOo0O00Oooo00OOO0OO0oooo() {
        return YsmBootstrapSupport.statusMessage();
    }
}
