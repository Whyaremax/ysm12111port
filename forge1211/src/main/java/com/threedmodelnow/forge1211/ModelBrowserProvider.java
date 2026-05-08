package com.threedmodelnow.forge1211;

import net.minecraft.client.gui.screens.Screen;

public interface ModelBrowserProvider {
    String id();

    String displayName();

    Screen createScreen();
}
