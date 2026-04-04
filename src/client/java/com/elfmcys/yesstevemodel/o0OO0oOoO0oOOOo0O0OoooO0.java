package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmBootstrapSupport;

public final class o0OO0oOoO0oOOOo0O0OoooO0 {
    private o0OO0oOoO0oOOOo0O0OoooO0() {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(boolean available) {
        if (!available) {
            YsmBootstrapSupport.setStatusMessage("YSM Java runtime disabled");
            return;
        }

        YsmBootstrapSupport.initializeClient();
    }
}
