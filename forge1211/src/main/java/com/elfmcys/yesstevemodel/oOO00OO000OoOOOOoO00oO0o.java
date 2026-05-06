package com.elfmcys.yesstevemodel;

import net.minecraftforge.common.ForgeConfigSpec;

public final class oOO00OO000OoOOOOoO00oO0o {
    public static ForgeConfigSpec.BooleanValue OoO0O0oO00O0o0OOOOoOOooo;
    public static ForgeConfigSpec.BooleanValue oo0Oo0oo0OOO00O0oO0o0O0O;
    public static ForgeConfigSpec.BooleanValue oOoOoO0OoOOoo00ooO0oO00o;
    public static ForgeConfigSpec.BooleanValue o0o0O0OOo0O0O0oO00oooO00;
    public static ForgeConfigSpec.BooleanValue oOo0O00Oooo00OOO0OO0oooo;
    public static ForgeConfigSpec.BooleanValue o000ooo0oO0O0ooOO00oo0o0;
    public static ForgeConfigSpec.BooleanValue oOooooOooO0oO0ooOOoo00oo;
    public static ForgeConfigSpec.BooleanValue ooOO0oo000O00OOoooO0o0Oo;
    public static ForgeConfigSpec.BooleanValue OO00Oooo0oo0o00o0OoOOooo;
    public static ForgeConfigSpec.DoubleValue OO00OooO0O0oo000O0oo0ooO;
    public static ForgeConfigSpec.BooleanValue O0o0O00O0oOOoOOoo00OO0o0;

    static {
        OoO0O0oO00O0o0OOOOoOOooo();
    }

    private oOO00OO000OoOOOOoO00oO0o() {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        OoO0O0oO00O0o0OOOOoOOooo(builder);
        o0ooo0O0OOO0000OOooo000o.OoO0O0oO00O0o0OOOOoOOooo(builder);
        o0Oo0OOO00o00OoooO0oO0o0.OoO0O0oO00O0o0OOOOoOOooo(builder);
        builder.build();
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(ForgeConfigSpec.Builder builder) {
        builder.push("general");

        builder.comment("Whether to display disclaimer GUI");
        OoO0O0oO00O0o0OOOOoOOooo = builder.define("DisclaimerShow", true);

        builder.comment("Whether to print animation roulette play message");
        oo0Oo0oo0OOO00O0oO0o0O0O = builder.define("PrintAnimationRouletteMsg", false);

        builder.comment("Prevents rendering of self player's model");
        oOoOoO0OoOOoo00ooO0oO00o = builder.define("DisableSelfModel", false);

        builder.comment("Prevents rendering of other player's model");
        o0o0O0OOo0O0O0oO00oooO00 = builder.define("DisableOtherModel", false);

        builder.comment("Prevents rendering of self player's hand");
        oOo0O00Oooo00OOO0OO0oooo = builder.define("DisableSelfHands", false);

        builder.comment("Prevents rendering of projectile model");
        o000ooo0oO0O0ooOO00oo0o0 = builder.define("DisableProjectileModel", false);

        builder.comment("Prevents rendering of vehicle model");
        oOooooOooO0oO0ooOOoo00oo = builder.define("DisableVehicleModel", false);

        builder.comment("Disable first person animation from other mods.");
        ooOO0oo000O00OOoooO0o0Oo = builder.define("DisableExternalFirstPersonAnim", false);

        builder.comment("If rendering errors occur, try turning on this.");
        OO00Oooo0oo0o00o0OoOOooo = builder.define("UseCompatibilityRenderer", false);

        builder.comment("The amount of volume when the animation is played.");
        OO00OooO0O0oo000O0oo0ooO = builder.defineInRange("SoundVolume", 100.0D, 0.0D, 100.0D);

        builder.comment("Whether to display model ID first in the model selection screen, instead of the model name filled in by the model author.");
        O0o0O00O0oOOoOOoo00OO0o0 = builder.define("ShowModelIdFirst", false);

        builder.pop();
    }
}
