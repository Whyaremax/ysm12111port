package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmClientRuntime;
import com.elfmcys.yesstevemodel.runtime.YsmPackDescriptor;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class YsmTextureSelectionScreen extends Screen {
    private static final int PAGE_SIZE = 12;

    private final Screen parent;
    private final YsmPackDescriptor descriptor;
    private final Consumer<String> selectionCallback;
    private String selectedTextureId;
    private int currentPage;

    public YsmTextureSelectionScreen(
        Screen parent,
        YsmPackDescriptor descriptor,
        String selectedTextureId,
        Consumer<String> selectionCallback
    ) {
        super(Component.literal("YSM Textures"));
        this.parent = parent;
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
        this.selectedTextureId = normalizeTextureId(selectedTextureId, descriptor);
        this.selectionCallback = Objects.requireNonNull(selectionCallback, "selectionCallback");
    }

    @Override
    protected void init() {
        this.rebuildWidgets();
    }

    @Override
    public void onClose() {
        this.selectionCallback.accept(this.selectedTextureId);
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        this.renderMenuBackground(context);
        super.render(context, mouseX, mouseY, deltaTicks);

        int leftX = 18;
        int topY = 18;
        int panelWidth = this.width - 36;
        List<String> textures = this.descriptor.textureIds();
        int totalPages = Math.max(1, (textures.size() + PAGE_SIZE - 1) / PAGE_SIZE);

        context.drawString(this.font, this.title, leftX, topY, 0xFFFFFF);
        context.drawString(
            this.font,
            Component.literal(this.descriptor.displayName()),
            leftX,
            topY + 14,
            0xC0C0C0
        );
        context.drawString(
            this.font,
            Component.literal("Selected texture: " + this.selectedTextureId),
            leftX,
            topY + 28,
            0xD0D0D0
        );
        context.drawString(
            this.font,
            Component.literal("Page " + (this.currentPage + 1) + " / " + totalPages + "    " + textures.size() + " texture(s)"),
            leftX,
            topY + 42,
            0xA8A8A8
        );
        context.fill(leftX - 4, topY + 58, leftX + panelWidth, this.height - 42, 0x44202020);
    }

    protected void rebuildWidgets() {
        this.clearWidgets();

        int leftX = 18;
        int topY = 18;
        int panelWidth = this.width - 36;
        int buttonWidth = Math.max(96, (panelWidth - 24) / 3);
        int listStartY = topY + 68;
        List<String> textures = this.descriptor.textureIds();
        int pageCount = Math.max(1, (textures.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        this.currentPage = Math.max(0, Math.min(this.currentPage, pageCount - 1));
        int from = this.currentPage * PAGE_SIZE;
        int to = Math.min(textures.size(), from + PAGE_SIZE);

        for (int index = from; index < to; index++) {
            String textureId = textures.get(index);
            int localIndex = index - from;
            int column = localIndex % 3;
            int row = localIndex / 3;
            int x = leftX + column * (buttonWidth + 8);
            int y = listStartY + row * 24;
            String label = textureId.equals(this.selectedTextureId) ? "▶ " + textureId : textureId;
            if (label.length() > 34) {
                label = label.substring(0, 33) + "…";
            }
            this.addRenderableWidget(Button.builder(Component.literal(label), button -> {
                this.selectedTextureId = textureId;
                this.selectionCallback.accept(textureId);
                this.rebuildWidgets();
            }).bounds(x, y, buttonWidth, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.translatable("gui.yes_steve_model.pre_page"), button -> {
            this.currentPage = Math.max(0, this.currentPage - 1);
            this.rebuildWidgets();
        }).bounds(leftX, this.height - 38, 86, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.yes_steve_model.next_page"), button -> {
            this.currentPage = Math.min(pageCount - 1, this.currentPage + 1);
            this.rebuildWidgets();
        }).bounds(leftX + 92, this.height - 38, 86, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Apply"), button -> {
            this.selectionCallback.accept(this.selectedTextureId);
            YsmClientRuntime.apply(this.descriptor, this.selectedTextureId);
            this.onClose();
        }).bounds(this.width - 190, this.height - 38, 80, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.yes_steve_model.close"), button -> this.onClose())
            .bounds(this.width - 104, this.height - 38, 86, 20)
            .build());
    }

    private static String normalizeTextureId(String textureId, YsmPackDescriptor descriptor) {
        List<String> textures = descriptor.textureIds();
        if (textureId != null && textures.contains(textureId)) {
            return textureId;
        }
        if (textures.contains(descriptor.defaultTextureId())) {
            return descriptor.defaultTextureId();
        }
        return textures.isEmpty() ? "default" : textures.getFirst();
    }
}
