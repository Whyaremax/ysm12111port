package com.elfmcys.yesstevemodel;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public final class oooo0oo00o0oooOOOoOOO00O {
    public static ForgeConfigSpec.IntValue OoO0O0oO00O0o0OOOOoOOooo;
    public static ForgeConfigSpec.IntValue oo0Oo0oo0OOO00O0oO0o0O0O;
    public static ForgeConfigSpec.IntValue oOoOoO0OoOOoo00ooO0oO00o;
    public static ForgeConfigSpec.BooleanValue o0o0O0OOo0O0O0oO00oooO00;
    public static ForgeConfigSpec.BooleanValue oOo0O00Oooo00OOO0OO0oooo;
    public static ForgeConfigSpec.ConfigValue<String> o000ooo0oO0O0ooOO00oo0o0;
    public static ForgeConfigSpec.ConfigValue<String> oOooooOooO0oO0ooOOoo00oo;
    public static ForgeConfigSpec.IntValue ooOO0oo000O00OOoooO0o0Oo;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> OO00Oooo0oo0o00o0OoOOooo;

    static {
        OoO0O0oO00O0o0OOOOoOOooo();
    }

    private oooo0oo00o0oooOOOoOOO00O() {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        OoO0O0oO00O0o0OOOOoOOooo(builder);
        builder.build();
    }

    private static void OoO0O0oO00O0o0OOOOoOOooo(ForgeConfigSpec.Builder builder) {
        builder.comment("The default model ID when a player first enters the game");
        o000ooo0oO0O0ooOO00oo0o0 = builder.define("DefaultModelId", "default");

        builder.comment("The default model texture when a player first enters the game");
        oOooooOooO0oO0ooOOoo00oo = builder.define("DefaultModelTexture", "default");

        builder.comment("Whether or not players are allowed to switch models");
        oOo0O00Oooo00OOO0OO0oooo = builder.define("CanSwitchModel", true);

        builder.comment("Models that are not displayed on the client model selection screen");
        builder.comment("Example: [\"default\", \"misc_3_default_boy\", \"misc_1_alex\", \"misc_2_steve\", \"wine_fox_1_taisho_maid\", \"wine_fox_7_jk\"]");
        OO00Oooo0oo0o00o0OoOOooo = builder.define("ClientNotDisplayModels", Lists.<String>newArrayList());

        builder.push("server_scheduler");

        builder.comment("Concurrent level for processing models. Value 0 means AUTO.");
        OoO0O0oO00O0o0OOOOoOOooo = builder.defineInRange(
            "ThreadCount",
            0,
            0,
            Math.max(2, Runtime.getRuntime().availableProcessors() - 1)
        );

        builder.comment("Bandwidth limitation during distributing models to players.(In Mbps)");
        oo0Oo0oo0OOO00O0oO0o0O0O = builder.defineInRange("BandwidthLimit", 5, 1, 999);

        builder.comment("Timeout for players to respond to synchronization. Value not greater than 10 means AUTO.(In seconds)");
        oOoOoO0OoOOoo00ooO0oO00o = builder.defineInRange("PlayerSyncTimeout", 0, 0, 120);

        builder.comment("Suppress network synchronization of partial features to reduce bandwidth usage");
        builder.comment("Only effective when there are tons of players");
        o0o0O0OOo0O0O0oO00oooO00 = builder.define("LowBandwidthUsage", false);

        builder.comment("Skip sound effect processing to reduce server bandwidth and client memory usage");
        builder.comment("0: Accept all sounds (Default)");
        builder.comment("1: Accept short sounds only (Shorter than 4s and smaller than 40KB)");
        builder.comment("2: Reject all sounds (Not recommended)");
        builder.comment("Note: Takes effect after model reloading. Increasing this option does not cause model resynchronization, whereas decreasing it does.");
        ooOO0oo000O00OOoooO0o0Oo = builder.defineInRange("AcceptSoundFX", 0, 0, 2);

        builder.pop();
    }
}
