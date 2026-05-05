package com.threedmodelnow.fabric1211;

import com.threedmodelnow.core.ThreeDModelNow;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class ModelSelectionKeyBindings {
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of(ThreeDModelNow.MOD_ID, "model_selection"));

    public static final KeyBinding OPEN_MODEL_SELECTION = new KeyBinding(
        "key.threed_model_now.player_model",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_Y,
        CATEGORY
    );

    private static boolean registered;

    private ModelSelectionKeyBindings() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }

        KeyBindingHelper.registerKeyBinding(OPEN_MODEL_SELECTION);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MODEL_SELECTION.wasPressed()) {
                openModelSelection(client);
            }
        });
        registered = true;
    }

    private static void openModelSelection(MinecraftClient client) {
        if (client != null) {
            client.setScreen(new ModelBrowserScreen());
        }
    }
}
