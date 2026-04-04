package com.elfmcys.yesstevemodel;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class O00OoOO0oOOOo00O00o0o0oo {
    public static final int OoO0O0oO00O0o0OOOOoOOooo = 1;
    public static final int oo0Oo0oo0OOO00O0oO0o0O0O = 2;
    public static final int oOoOoO0OoOOoo00ooO0oO00o = 4;
    public static final int o0o0O0OOo0O0O0oO00oooO00 = 8;
    public static final String oOo0O00Oooo00OOO0OO0oooo = "key.category.yes_steve_model";
    static final List<oOOoo0O00oOOOOO0OO0o00OO> o000ooo0oO0O0ooOO00oo0o0 = new ArrayList<>();

    private final String oOooooOooO0oO0ooOOoo00oo;
    private String ooOO0oo000O00OOoooO0o0Oo = oOo0O00Oooo00OOO0OO0oooo;
    private InputUtil.Type OO00Oooo0oo0o00o0OoOOooo = InputUtil.Type.KEYSYM;
    private int OO00OooO0O0oo000O0oo0ooO = -1;
    private int O0o0O00O0oOOoOOoo00OO0o0 = 0;
    private OOO0OOO0O0OooO0oO0Oo0O00 OOo000O0o00OOO0ooOOoo00o = O00OoOO0oOOOo00O00o0o0oo::OoO0O0oO00O0o0OOOOoOOooo;

    public O00OoOO0oOOOo00O00o0o0oo(String id) {
        this.oOooooOooO0oO0ooOOoo00oo = id;
    }

    public String OoO0O0oO00O0o0OOOOoOOooo() {
        return this.oOooooOooO0oO0ooOOoo00oo;
    }

    public String oo0Oo0oo0OOO00O0oO0o0O0O() {
        return this.ooOO0oo000O00OOoooO0o0Oo;
    }

    public O00OoOO0oOOOo00O00o0o0oo OoO0O0oO00O0o0OOOOoOOooo(String categoryKey) {
        this.ooOO0oo000O00OOoooO0o0Oo = categoryKey;
        return this;
    }

    public InputUtil.Type oOoOoO0OoOOoo00ooO0oO00o() {
        return this.OO00Oooo0oo0o00o0OoOOooo;
    }

    public O00OoOO0oOOOo00O00o0o0oo OoO0O0oO00O0o0OOOOoOOooo(InputUtil.Type type) {
        this.OO00Oooo0oo0o00o0OoOOooo = type;
        return this;
    }

    public int o0o0O0OOo0O0O0oO00oooO00() {
        return this.OO00OooO0O0oo000O0oo0ooO;
    }

    public O00OoOO0oOOOo00O00o0o0oo OoO0O0oO00O0o0OOOOoOOooo(int keyCode) {
        this.OO00OooO0O0oo000O0oo0ooO = keyCode;
        return this;
    }

    public OOO0OOO0O0OooO0oO0Oo0O00 oOo0O00Oooo00OOO0OO0oooo() {
        return this.OOo000O0o00OOO0ooOOoo00o;
    }

    public O00OoOO0oOOOo00O00o0o0oo OoO0O0oO00O0o0OOOOoOOooo(OOO0OOO0O0OooO0oO0Oo0O00 handler) {
        this.OOo000O0o00OOO0ooOOoo00o = handler;
        return this;
    }

    public O00OoOO0oOOOo00O00o0o0oo OoO0O0oO00O0o0OOOOoOOooo(O0O0O00OOO0oO0o0o0o0000O handler) {
        this.OOo000O0o00OOO0ooOOoo00o = handler;
        return this;
    }

    public O00OoOO0oOOOo00O00o0o0oo oo0Oo0oo0OOO00O0oO0o0O0O(int flags) {
        this.O0o0O00O0oOOoOOoo00OO0o0 = flags;
        return this;
    }

    public O00OoOO0oOOOo00O00o0o0oo o000ooo0oO0O0ooOO00oo0o0() {
        this.O0o0O00O0oOOoOOoo00OO0o0 |= oOoOoO0OoOOoo00ooO0oO00o;
        return this;
    }

    public O00OoOO0oOOOo00O00o0o0oo oOooooOooO0oO0ooOOoo00oo() {
        this.O0o0O00O0oOOoOOoo00OO0o0 |= OoO0O0oO00O0o0OOOOoOOooo;
        return this;
    }

    public O00OoOO0oOOOo00O00o0o0oo ooOO0oo000O00OOoooO0o0Oo() {
        this.O0o0O00O0oOOoOOoo00OO0o0 |= oo0Oo0oo0OOO00O0oO0o0O0O;
        return this;
    }

    public boolean OO00Oooo0oo0o00o0OoOOooo() {
        return (this.O0o0O00O0oOOoOOoo00OO0o0 & oo0Oo0oo0OOO00O0oO0o0O0O) == oo0Oo0oo0OOO00O0oO0o0O0O;
    }

    public boolean OO00OooO0O0oo000O0oo0ooO() {
        return (this.O0o0O00O0oOOoOOoo00OO0o0 & oOoOoO0OoOOoo00ooO0oO00o) == oOoOoO0OoOOoo00ooO0oO00o;
    }

    public boolean O0o0O00O0oOOoOOoo00OO0o0() {
        return (this.O0o0O00O0oOOoOOoo00OO0o0 & OoO0O0oO00O0o0OOOOoOOooo) == OoO0O0oO00O0o0OOOOoOOooo;
    }

    public KeyBinding OOo000O0o00OOO0ooOOoo00o() {
        for (oOOoo0O00oOOOOO0OO0o00OO factory : o000ooo0oO0O0ooOO00oo0o0) {
            KeyBinding binding = factory.build(this);
            if (binding != null) {
                return binding;
            }
        }

        return new OoO0O0oO00O0o0OOOOoOOooo(
            this.oOooooOooO0oO0ooOOoo00oo,
            this.OO00Oooo0oo0o00o0OoOOooo,
            this.OO00OooO0O0oo000O0oo0ooO,
            this.ooOO0oo000O00OOoooO0o0Oo,
            this.OOo000O0o00OOO0ooOOoo00o
        );
    }

    private static void OoO0O0oO00O0o0OOOOoOOooo(KeyBinding keyBinding, boolean pressed) {
    }

    static class OoO0O0oO00O0o0OOOOoOOooo extends KeyBinding {
        private final OOO0OOO0O0OooO0oO0Oo0O00 OoO0O0oO00O0o0OOOOoOOooo;

        OoO0O0oO00O0o0OOOOoOOooo(
            String id,
            InputUtil.Type type,
            int code,
            String categoryKey,
            OOO0OOO0O0OooO0oO0Oo0O00 handler
        ) {
            super(id, type, code, KeyBinding.Category.MISC, 0);
            this.OoO0O0oO00O0o0OOOOoOOooo = handler;
        }

        @Override
        public void setPressed(boolean pressed) {
            super.setPressed(pressed);
            try {
                this.OoO0O0oO00O0o0OOOOoOOooo.handle(this, pressed);
            } catch (Throwable throwable) {
                YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn(
                    "Disabled failing YSM key handler {} on 1.21.11",
                    this.getId(),
                    throwable
                );
                this.setPressed(false);
            }
        }
    }
}
