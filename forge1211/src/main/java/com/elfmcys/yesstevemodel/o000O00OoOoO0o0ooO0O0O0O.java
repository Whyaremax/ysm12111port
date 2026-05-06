package com.elfmcys.yesstevemodel;

import java.lang.reflect.Method;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;

public final class o000O00OoOoO0o0ooO0O0O0O {
    private static boolean OoO0O0oO00O0o0OOOOoOOooo;

    private o000O00OoOoO0o0ooO0O0O0O() {
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo(Minecraft client) {
        if (OoO0O0oO00O0o0OOOOoOOooo) {
            return;
        }

        LocalPlayer player = client.player;
        if (player == null || !OoO0O0oO00O0o0OOOOoOOooo(player)) {
            return;
        }

        oo0Oo0oo0OOO00O0oO0o0O0O(player).ifPresent(o000O00OoOoO0o0ooO0O0O0O::OoO0O0oO00O0o0OOOOoOOooo);
    }

    public static boolean OoO0O0oO00O0o0OOOOoOOooo(LocalPlayer player) {
        ClientInput input = player.input;
        if (input == null) {
            return false;
        }

        Vec2 movement = input.getMoveVector();
        if (movement != null && (OoO0O0oO00O0o0OOOOoOOooo(movement.x) || OoO0O0oO00O0o0OOOOoOOooo(movement.y))) {
            return true;
        }

        Input playerInput = input.keyPresses;
        return playerInput != null && (playerInput.jump() || playerInput.shift());
    }

    private static boolean OoO0O0oO00O0o0OOOOoOOooo(float value) {
        return Math.abs(value) > 1.0E-5F;
    }

    public static void OoO0O0oO00O0o0OOOOoOOooo() {
        OoO0O0oO00O0o0OOOOoOOooo = !OoO0O0oO00O0o0OOOOoOOooo;
    }

    public static boolean oo0Oo0oo0OOO00O0oO0o0O0O() {
        return OoO0O0oO00O0o0OOOOoOOooo;
    }

    private static Optional<OO00OooO0O0oo000O0oo0ooO> oo0Oo0oo0OOO00O0oO0o0O0O(LocalPlayer player) {
        for (String getterName : new String[]{"ooOO0oo000O00OOoooO0o0Oo", "o0o0O0OOo0O0O0oO00oooO00"}) {
            try {
                Method getter = player.getClass().getMethod(getterName);
                Object attachmentContainer = getter.invoke(player);
                if (attachmentContainer == null) {
                    continue;
                }

                Class<?> attachmentClass = Class.forName("com.elfmcys.yesstevemodel.O0o0O00O0oOOoOOoo00OO0o0");
                Object key = attachmentClass.getField("OoO0O0oO00O0o0OOOOoOOooo").get(null);
                Method lookup = attachmentContainer.getClass().getMethod("OoO0O0oO00O0o0OOOOoOOooo", key.getClass());
                Object result = lookup.invoke(attachmentContainer, key);
                if (result instanceof Optional<?> optional) {
                    Object value = optional.orElse(null);
                    if (value instanceof OO00OooO0O0oo000O0oo0ooO state) {
                        return Optional.of(state);
                    }
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return Optional.empty();
    }

    private static void OoO0O0oO00O0o0OOOOoOOooo(OO00OooO0O0oo000O0oo0ooO state) {
        if (!state.O0o0O00O0oOOoOOoo00OO0o0()) {
            return;
        }

        state.O00ooOO0oOO0OOOo00o0oooO();
        if (OO000oo00oo0O0O0oO0OoOoo.OoO0O0oO00O0o0OOOOoOOooo()) {
            OO000oo00oo0O0O0oO0OoOoo.OoO0O0oO00O0o0OOOOoOOooo(Oo0Oo0OOOo0O000oOo00ooO0.OoO0O0oO00O0o0OOOOoOOooo());
        }
    }
}
