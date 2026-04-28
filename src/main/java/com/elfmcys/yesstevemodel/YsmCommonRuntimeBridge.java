package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmBootstrapSupport;

public final class YsmCommonRuntimeBridge {
    private YsmCommonRuntimeBridge() {
    }

    public static void setCommonRuntimeAvailable(boolean available) {
        if (!available) {
            YsmBootstrapSupport.setStatusMessage("YSM Java runtime disabled");
            o0o00OoOoo0oOO00Oooo0o0O.oo0Oo0oo0OOO00O0oO0o0O0O();
            return;
        }

        YsmBootstrapSupport.initializeCommon();
    }
}
