package com.elfmcys.yesstevemodel.runtime;

public record YsmScaleProfile(
    float scale,
    float worldTranslateY,
    float modelHeight,
    float modelMinY
) {
    public static final YsmScaleProfile DEFAULT = new YsmScaleProfile(1.0f, 0.0f, 32.0f, 0.0f);
}
