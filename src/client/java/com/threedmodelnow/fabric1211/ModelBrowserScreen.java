package com.threedmodelnow.fabric1211;

import com.threedmodelnow.core.ModelDisplayProvider;
import com.threedmodelnow.core.ModelFormatProvider;
import com.threedmodelnow.core.ModelImportProvider;
import com.threedmodelnow.core.ModelProviderRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class ModelBrowserScreen extends Screen {
    private static final int LINE_HEIGHT = 12;

    public ModelBrowserScreen() {
        super(Text.literal("3DModelNow Models"));
    }

    @Override
    protected void init() {
        int buttonY = this.height - 32;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> this.close())
            .dimensions(Math.max(10, this.width - 90), buttonY, 76, 20)
            .build());

        int y = 84;
        for (ModelBrowserProvider provider : ModelBrowserRegistry.providers()) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal(provider.displayName()), button -> {
                MinecraftClient.getInstance().setScreen(provider.createScreen());
            }).dimensions(18, y, Math.min(220, this.width - 36), 20).build());
            y += 24;
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderDarkening(context);
        super.render(context, mouseX, mouseY, deltaTicks);

        int x = 18;
        int y = 18;
        int width = Math.max(120, this.width - 36);
        context.drawTextWithShadow(this.textRenderer, this.title, x, y, 0xFFFFFF);
        y += 20;

        List<Text> lines = new ArrayList<>();
        List<ModelFormatProvider> formats = ModelProviderRegistry.formatProviders();
        List<ModelImportProvider> importers = ModelProviderRegistry.importProviders();
        List<ModelDisplayProvider> displays = ModelProviderRegistry.displayProviders();
        if (formats.isEmpty() && importers.isEmpty() && displays.isEmpty()) {
            lines.add(Text.literal("No optional model providers are loaded."));
            lines.add(Text.literal("Install a compatibility addon to enable format-specific importers and displays."));
        } else {
            lines.add(Text.literal("Loaded providers"));
            for (ModelFormatProvider provider : formats) {
                lines.add(Text.literal("Format: " + provider.displayName() + " (" + provider.id() + ")"));
            }
            for (ModelImportProvider provider : importers) {
                lines.add(Text.literal("Importer: " + provider.displayName() + " (" + provider.id() + ")"));
            }
            for (ModelDisplayProvider provider : displays) {
                lines.add(Text.literal("Display: " + provider.displayName() + " (" + provider.id() + ")"));
            }
        }

        for (Text line : lines) {
            context.drawWrappedTextWithShadow(this.textRenderer, line, x, y, width, 0xD0D0D0);
            y += this.textRenderer.wrapLines(line, width).size() * LINE_HEIGHT;
        }
    }
}
