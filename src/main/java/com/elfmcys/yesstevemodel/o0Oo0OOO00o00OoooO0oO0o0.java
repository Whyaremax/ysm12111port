package com.elfmcys.yesstevemodel;

import net.minecraftforge.common.ForgeConfigSpec;

public final class o0Oo0OOO00o00OoooO0oO0o0 {
    public static ForgeConfigSpec.BooleanValue OoO0O0oO00O0o0OOOOoOOooo;
    public static ForgeConfigSpec.EnumValue<PreviewStyle> oo0Oo0oo0OOO00O0oO0o0O0O;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        OoO0O0oO00O0o0OOOOoOOooo(builder);
        builder.build();
    }

    private o0Oo0OOO00o00OoooO0oO0o0() {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(ForgeConfigSpec.Builder builder) {
        builder.push("preview");
        builder.comment("Enable model preview overlay");
        OoO0O0oO00O0o0OOOOoOOooo = builder.define("EnablePreviewOverlay", false);
        builder.comment("Preview style");
        oo0Oo0oo0OOO00O0oO0o0O0O = builder.defineEnum("PreviewStyle", PreviewStyle.MINIMAL);
        builder.pop();
    }

    public enum PreviewStyle {
        MINIMAL,
        FULL
    }
}
