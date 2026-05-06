package com.threedmodelnow.forge1211;

import com.threedmodelnow.core.ModelDisplayProvider;
import com.threedmodelnow.core.ModelFormatProvider;
import com.threedmodelnow.core.ModelImportProvider;
import com.threedmodelnow.core.ModelProviderRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ModelBrowserScreen extends Screen {
    private static final int LINE_HEIGHT = 12;

    public ModelBrowserScreen() {
        super(Component.literal("3DModelNow Models"));
    }

    @Override
    protected void init() {
        int buttonY = this.height - 32;
        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> this.onClose())
            .bounds(Math.max(10, this.width - 90), buttonY, 76, 20)
            .build());

        int y = 84;
        for (ModelBrowserProvider provider : ModelBrowserRegistry.providers()) {
            this.addRenderableWidget(Button.builder(Component.literal(provider.displayName()), button -> {
                Minecraft.getInstance().setScreen(provider.createScreen());
            }).bounds(18, y, Math.min(220, this.width - 36), 20).build());
            y += 24;
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        this.renderMenuBackground(context);
        super.render(context, mouseX, mouseY, deltaTicks);

        int x = 18;
        int y = 18;
        int width = Math.max(120, this.width - 36);
        context.drawString(this.font, this.title, x, y, 0xFFFFFF);
        y += 20;

        List<Component> lines = new ArrayList<>();
        List<ModelFormatProvider> formats = ModelProviderRegistry.formatProviders();
        List<ModelImportProvider> importers = ModelProviderRegistry.importProviders();
        List<ModelDisplayProvider> displays = ModelProviderRegistry.displayProviders();
        if (formats.isEmpty() && importers.isEmpty() && displays.isEmpty()) {
            lines.add(Component.literal("No optional model providers are loaded."));
            lines.add(Component.literal("Install a compatibility addon to enable format-specific importers and displays."));
        } else {
            lines.add(Component.literal("Loaded providers"));
            for (ModelFormatProvider provider : formats) {
                lines.add(Component.literal("Format: " + provider.displayName() + " (" + provider.id() + ")"));
            }
            for (ModelImportProvider provider : importers) {
                lines.add(Component.literal("Importer: " + provider.displayName() + " (" + provider.id() + ")"));
            }
            for (ModelDisplayProvider provider : displays) {
                lines.add(Component.literal("Display: " + provider.displayName() + " (" + provider.id() + ")"));
            }
        }

        for (Component line : lines) {
            context.drawWordWrap(this.font, line, x, y, width, 0xD0D0D0);
            y += this.font.split(line, width).size() * LINE_HEIGHT;
        }
    }
}
