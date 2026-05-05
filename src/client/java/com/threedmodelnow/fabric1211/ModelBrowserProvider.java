package com.threedmodelnow.fabric1211;

import net.minecraft.client.gui.screen.Screen;

public interface ModelBrowserProvider {
    String id();

    String displayName();

    Screen createScreen();
}
