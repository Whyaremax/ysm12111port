package com.elfmcys.yesstevemodel;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class YsmOpenModelScreenKeyBinding {
    public static final KeyMapping OPEN_MODEL_SCREEN = new YsmKeyBindingBuilder("key.yes_steve_model.player_model.desc")
        .withDefaultKeyCode(89)
        .enableOptionFour()
        .withSimpleKeyHandler(YsmOpenModelScreenKeyBinding::openModelScreen)
        .build();

    private YsmOpenModelScreenKeyBinding() {
    }

    private static void openModelScreen(KeyMapping ignored) {
        Minecraft client = Minecraft.getInstance();
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
