package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmBootstrapSupport;

public final class O00o0OoO0ooO0oO0OO0Oo0Oo {
    private O00o0OoO0ooO0oO0OO0Oo0Oo() {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(boolean available) {
        if (!available) {
            YsmBootstrapSupport.setStatusMessage("YSM Java runtime disabled");
            o0o00OoOoo0oOO00Oooo0o0O.oo0Oo0oo0OOO00O0oO0o0O0O();
            return;
        }

        YsmBootstrapSupport.initializeCommon();
    }
}
