package com.threedmodelnow.forge1211;

import com.mojang.blaze3d.platform.InputConstants;
import com.threedmodelnow.core.ThreeDModelNow;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import org.lwjgl.glfw.GLFW;

public final class ModelSelectionKeyBindings {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ThreeDModelNow.MOD_ID, "model_selection"));

    public static final KeyMapping OPEN_MODEL_SELECTION = new KeyMapping(
        "key.threed_model_now.player_model",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_Y,
        CATEGORY
    );

    private static boolean registered;

    private ModelSelectionKeyBindings() {
    }

    public static synchronized void register(BusGroup modBus) {
        if (registered) {
            return;
        }

        RegisterKeyMappingsEvent.getBus(modBus).addListener(ModelSelectionKeyBindings::registerKeyMapping);
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> consumeOpenKey());
        registered = true;
    }

    private static void registerKeyMapping(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MODEL_SELECTION);
    }

    private static void consumeOpenKey() {
        while (OPEN_MODEL_SELECTION.consumeClick()) {
            openModelSelection(Minecraft.getInstance());
        }
    }

    private static void openModelSelection(Minecraft client) {
        if (client != null) {
            client.setScreen(new ModelBrowserScreen());
        }
    }
}
