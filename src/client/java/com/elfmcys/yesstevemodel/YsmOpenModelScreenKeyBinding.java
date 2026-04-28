package com.elfmcys.yesstevemodel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public final class YsmOpenModelScreenKeyBinding {
    public static final KeyBinding OPEN_MODEL_SCREEN = new YsmKeyBindingBuilder("key.yes_steve_model.player_model.desc")
        .withDefaultKeyCode(89)
        .enableOptionFour()
        .withSimpleKeyHandler(YsmOpenModelScreenKeyBinding::openModelScreen)
        .build();

    private YsmOpenModelScreenKeyBinding() {
    }

    private static void openModelScreen(KeyBinding ignored) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }

        if (!YesSteveModel.isAvailable()) {
            YesSteveModel.sendUnavailableStatusToClientPlayer();
            return;
        }

        client.setScreen(new YsmModelScreen());
    }
}
