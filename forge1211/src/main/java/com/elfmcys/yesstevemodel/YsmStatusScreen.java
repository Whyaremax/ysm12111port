package com.elfmcys.yesstevemodel;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class YsmStatusScreen extends Screen {
    private final List<Component> lines = new ArrayList<>();

    public YsmStatusScreen() {
        super(Component.literal("3DModelNow 1.21.11"));
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.lines.clear();
        this.lines.add(Component.literal("3DModelNow is replacing the old native-backed YSM client runtime with readable Java code."));
        this.lines.add(Component.literal("The current lane is focused on Fabric 1.21.11 YSM-compatible loading."));
        this.lines.add(Component.literal("Current status:"));
        this.lines.add(YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o());
        this.lines.add(Component.literal("Some original-client parity paths are still active rewrite work."));

        int buttonWidth = 140;
        int buttonY = this.height - 40;
        this.addRenderableWidget(
            Button.builder(Component.literal("Close"), button -> this.onClose())
                .bounds((this.width - buttonWidth) / 2, buttonY, buttonWidth, 20)
                .build()
        );
    }

    @Override
    public void onClose() {
        Minecraft client = Minecraft.getInstance();
        client.setScreen(null);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        int titleY = 24;
        context.drawCenteredString(this.font, this.title, this.width / 2, titleY, 0xFFFFFF);

        int x = Math.max(16, (this.width - 360) / 2);
        int y = 56;
        int maxWidth = Math.min(360, this.width - 32);
        for (Component line : this.lines) {
            context.drawWordWrap(this.font, line, x, y, maxWidth, 0xD0D0D0);
            y += this.font.split(line, maxWidth).size() * 9 + 8;
        }
    }
}
