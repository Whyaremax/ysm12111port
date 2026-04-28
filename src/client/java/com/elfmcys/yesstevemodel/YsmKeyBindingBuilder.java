package com.elfmcys.yesstevemodel;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class YsmKeyBindingBuilder {
    public static final int FLAG_OPTION_ONE = 1;
    public static final int FLAG_OPTION_TWO = 2;
    public static final int FLAG_OPTION_FOUR = 4;
    public static final int FLAG_OPTION_EIGHT = 8;
    public static final String DEFAULT_CATEGORY_KEY = "key.category.yes_steve_model";

    static final List<oOOoo0O00oOOOOO0OO0o00OO> CUSTOM_FACTORIES = new ArrayList<>();

    private final String translationKey;
    private String categoryKey = DEFAULT_CATEGORY_KEY;
    private InputUtil.Type inputType = InputUtil.Type.KEYSYM;
    private int defaultKeyCode = -1;
    private int flags = 0;
    private OOO0OOO0O0OooO0oO0Oo0O00 keyHandler = YsmKeyBindingBuilder::ignoreKeyStateChange;

    public YsmKeyBindingBuilder(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return this.translationKey;
    }

    public String categoryKey() {
        return this.categoryKey;
    }

    public YsmKeyBindingBuilder withCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
        return this;
    }

    public InputUtil.Type inputType() {
        return this.inputType;
    }

    public YsmKeyBindingBuilder withInputType(InputUtil.Type inputType) {
        this.inputType = inputType;
        return this;
    }

    public int defaultKeyCode() {
        return this.defaultKeyCode;
    }

    public YsmKeyBindingBuilder withDefaultKeyCode(int defaultKeyCode) {
        this.defaultKeyCode = defaultKeyCode;
        return this;
    }

    public OOO0OOO0O0OooO0oO0Oo0O00 keyHandler() {
        return this.keyHandler;
    }

    public YsmKeyBindingBuilder withKeyHandler(OOO0OOO0O0OooO0oO0Oo0O00 keyHandler) {
        this.keyHandler = keyHandler;
        return this;
    }

    public YsmKeyBindingBuilder withSimpleKeyHandler(O0O0O00OOO0oO0o0o0o0000O keyHandler) {
        this.keyHandler = keyHandler;
        return this;
    }

    public YsmKeyBindingBuilder withFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public YsmKeyBindingBuilder enableOptionFour() {
        this.flags |= FLAG_OPTION_FOUR;
        return this;
    }

    public YsmKeyBindingBuilder enableOptionOne() {
        this.flags |= FLAG_OPTION_ONE;
        return this;
    }

    public YsmKeyBindingBuilder enableOptionTwo() {
        this.flags |= FLAG_OPTION_TWO;
        return this;
    }

    public boolean hasOptionTwo() {
        return (this.flags & FLAG_OPTION_TWO) == FLAG_OPTION_TWO;
    }

    public boolean hasOptionFour() {
        return (this.flags & FLAG_OPTION_FOUR) == FLAG_OPTION_FOUR;
    }

    public boolean hasOptionOne() {
        return (this.flags & FLAG_OPTION_ONE) == FLAG_OPTION_ONE;
    }

    public KeyBinding build() {
        for (oOOoo0O00oOOOOO0OO0o00OO factory : CUSTOM_FACTORIES) {
            KeyBinding binding = factory.build(this);
            if (binding != null) {
                return binding;
            }
        }

        return new HandlingKeyBinding(
            this.translationKey,
            this.inputType,
            this.defaultKeyCode,
            this.categoryKey,
            this.keyHandler
        );
    }

    private static void ignoreKeyStateChange(KeyBinding keyBinding, boolean pressed) {
    }

    static class HandlingKeyBinding extends KeyBinding {
        private final OOO0OOO0O0OooO0oO0Oo0O00 keyHandler;

        HandlingKeyBinding(
            String translationKey,
            InputUtil.Type inputType,
            int defaultKeyCode,
            String categoryKey,
            OOO0OOO0O0OooO0oO0Oo0O00 keyHandler
        ) {
            super(translationKey, inputType, defaultKeyCode, KeyBinding.Category.MISC, 0);
            this.keyHandler = keyHandler;
        }

        @Override
        public void setPressed(boolean pressed) {
            super.setPressed(pressed);
            try {
                this.keyHandler.handle(this, pressed);
            } catch (Throwable throwable) {
                YesSteveModel.LOGGER.warn(
                    "Disabled failing YSM key handler {} on 1.21.11",
                    this.getId(),
                    throwable
                );
                this.setPressed(false);
            }
        }
    }
}
