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
        super(Text.literal("3DModelNow 1.21.11"));
    }

    @Override
    protected void init() {
        this.clearChildren();
        this.lines.clear();
        this.lines.add(Text.literal("3DModelNow is replacing the old native-backed YSM client runtime with readable Java code."));
        this.lines.add(Text.literal("The current lane is focused on Fabric 1.21.11 YSM-compatible loading."));
        this.lines.add(Text.literal("Current status:"));
        this.lines.add(YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o());
        this.lines.add(Text.literal("Some original-client parity paths are still active rewrite work."));

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
