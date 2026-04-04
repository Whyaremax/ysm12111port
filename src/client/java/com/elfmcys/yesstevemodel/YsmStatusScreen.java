package com.elfmcys.yesstevemodel;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class YsmStatusScreen extends Screen {
    private final List<Text> lines = new ArrayList<>();

    public YsmStatusScreen() {
        super(Text.literal("YSM 1.21.11 Port"));
    }

    @Override
    protected void init() {
        this.clearChildren();
        this.lines.clear();
        this.lines.add(Text.literal("The original YSM UI is not ported yet."));
        this.lines.add(Text.literal("This build is currently keeping the client stable while the old 1.21.1 runtime is being replaced."));
        this.lines.add(Text.literal("Current status:"));
        this.lines.add(YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o());
        this.lines.add(Text.literal("Default model application is not working yet because the remaining model/runtime path is still incomplete."));

        int buttonWidth = 140;
        int buttonY = this.height - 40;
        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Close"), button -> this.close())
                .dimensions((this.width - buttonWidth) / 2, buttonY, buttonWidth, 20)
                .build()
        );
    }

    @Override
    public void close() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        int titleY = 24;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, titleY, 0xFFFFFF);

        int x = Math.max(16, (this.width - 360) / 2);
        int y = 56;
        int maxWidth = Math.min(360, this.width - 32);
        for (Text line : this.lines) {
            context.drawWrappedTextWithShadow(this.textRenderer, line, x, y, maxWidth, 0xD0D0D0);
            y += this.textRenderer.wrapLines(line, maxWidth).size() * 9 + 8;
        }
    }
}
