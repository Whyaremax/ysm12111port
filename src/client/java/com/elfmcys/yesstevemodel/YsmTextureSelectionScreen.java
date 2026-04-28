package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmClientRuntime;
import com.elfmcys.yesstevemodel.runtime.YsmPackDescriptor;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

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
        super(Text.literal("YSM Textures"));
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
    public void close() {
        this.selectionCallback.accept(this.selectedTextureId);
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderDarkening(context);
        super.render(context, mouseX, mouseY, deltaTicks);

        int leftX = 18;
        int topY = 18;
        int panelWidth = this.width - 36;
        List<String> textures = this.descriptor.textureIds();
        int totalPages = Math.max(1, (textures.size() + PAGE_SIZE - 1) / PAGE_SIZE);

        context.drawTextWithShadow(this.textRenderer, this.title, leftX, topY, 0xFFFFFF);
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal(this.descriptor.displayName()),
            leftX,
            topY + 14,
            0xC0C0C0
        );
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Selected texture: " + this.selectedTextureId),
            leftX,
            topY + 28,
            0xD0D0D0
        );
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Page " + (this.currentPage + 1) + " / " + totalPages + "    " + textures.size() + " texture(s)"),
            leftX,
            topY + 42,
            0xA8A8A8
        );
        context.fill(leftX - 4, topY + 58, leftX + panelWidth, this.height - 42, 0x44202020);
    }

    private void rebuildWidgets() {
        this.clearChildren();

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
            this.addDrawableChild(ButtonWidget.builder(Text.literal(label), button -> {
                this.selectedTextureId = textureId;
                this.selectionCallback.accept(textureId);
                this.rebuildWidgets();
            }).dimensions(x, y, buttonWidth, 20).build());
        }

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.yes_steve_model.pre_page"), button -> {
            this.currentPage = Math.max(0, this.currentPage - 1);
            this.rebuildWidgets();
        }).dimensions(leftX, this.height - 38, 86, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.yes_steve_model.next_page"), button -> {
            this.currentPage = Math.min(pageCount - 1, this.currentPage + 1);
            this.rebuildWidgets();
        }).dimensions(leftX + 92, this.height - 38, 86, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Apply"), button -> {
            this.selectionCallback.accept(this.selectedTextureId);
            YsmClientRuntime.apply(this.descriptor, this.selectedTextureId);
            this.close();
        }).dimensions(this.width - 190, this.height - 38, 80, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.yes_steve_model.close"), button -> this.close())
            .dimensions(this.width - 104, this.height - 38, 86, 20)
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
