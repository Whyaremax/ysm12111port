package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmBootstrapSupport;

public final class YsmCommonRuntimeBridge {
    private YsmCommonRuntimeBridge() {
    }

    public static void setCommonRuntimeAvailable(boolean available) {
        if (!available) {
            YsmBootstrapSupport.setStatusMessage("YSM Java runtime disabled");
            return;
        }

        YsmBootstrapSupport.initializeCommon();
    }
}
