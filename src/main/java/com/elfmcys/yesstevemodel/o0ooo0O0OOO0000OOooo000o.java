package com.elfmcys.yesstevemodel;

import net.minecraftforge.common.ForgeConfigSpec;

public final class o0ooo0O0OOO0000OOooo000o {
    public static ForgeConfigSpec.BooleanValue OoO0O0oO00O0o0OOOOoOOooo;
    public static ForgeConfigSpec.IntValue oo0Oo0oo0OOO00O0oO0o0O0O;
    public static ForgeConfigSpec.IntValue oOoOoO0OoOOoo00ooO0oO00o;
    public static ForgeConfigSpec.DoubleValue o0o0O0OOo0O0O0oO00oooO00;
    public static ForgeConfigSpec.DoubleValue oOo0O00Oooo00OOO0OO0oooo;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        OoO0O0oO00O0o0OOOOoOOooo(builder);
        builder.build();
    }

    private o0ooo0O0OOO0000OOooo000o() {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(ForgeConfigSpec.Builder builder) {
        builder.push("overlay");
        builder.comment("Enable loading overlay");
        OoO0O0oO00O0o0OOOOoOOooo = builder.define("EnableLoadingOverlay", false);
        builder.comment("Overlay X position");
        oo0Oo0oo0OOO00O0oO0o0O0O = builder.defineInRange("OverlayX", 10, -8192, 8192);
        builder.comment("Overlay Y position");
        oOoOoO0OoOOoo00ooO0oO00o = builder.defineInRange("OverlayY", 10, -8192, 8192);
        builder.comment("Overlay scale");
        o0o0O0OOo0O0O0oO00oooO00 = builder.defineInRange("OverlayScale", 1.0D, 0.1D, 10.0D);
        builder.comment("Preview scale");
        oOo0O00Oooo00OOO0OO0oooo = builder.defineInRange("PreviewScale", 1.0D, 0.1D, 10.0D);
        builder.pop();
    }
}
