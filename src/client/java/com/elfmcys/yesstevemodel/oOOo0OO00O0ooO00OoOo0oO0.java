package com.elfmcys.yesstevemodel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public final class oOOo0OO00O0ooO00OoOo0oO0 {
    public static final KeyBinding OoO0O0oO00O0o0OOOOoOOooo;

    static {
        OoO0O0oO00O0o0OOOOoOOooo = new O00OoOO0oOOOo00O00o0o0oo("key.yes_steve_model.player_model.desc")
            .OoO0O0oO00O0o0OOOOoOOooo(89)
            .o000ooo0oO0O0ooOO00oo0o0()
            .OoO0O0oO00O0o0OOOOoOOooo((O0O0O00OOO0oO0o0o0o0000O) oOOo0OO00O0ooO00OoOo0oO0::OoO0O0oO00O0o0OOOOoOOooo)
            .OOo000O0o00OOO0ooOOoo00o();
    }

    private oOOo0OO00O0ooO00OoOo0oO0() {
    }

    private static void OoO0O0oO00O0o0OOOOoOOooo(KeyBinding ignored) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }

        if (!YesSteveModel.isAvailable()) {
            YesSteveModel.oo0Oo0oo0OOO00O0oO0o0O0O();
            return;
        }

        client.setScreen(new YsmModelScreen());
    }
}
