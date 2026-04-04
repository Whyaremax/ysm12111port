package com.elfmcys.yesstevemodel;

import com.elfmcys.yesstevemodel.runtime.YsmActiveSelection;
import com.elfmcys.yesstevemodel.runtime.YsmClientRuntime;
import com.elfmcys.yesstevemodel.runtime.YsmCompiledPack;
import com.elfmcys.yesstevemodel.runtime.YsmPackDescriptor;
import com.elfmcys.yesstevemodel.runtime.YsmRenderBridge;
import com.elfmcys.yesstevemodel.runtime.YsmSourcePack;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class YsmModelScreen extends Screen {
    private static final int PAGE_SIZE = 8;

    private List<YsmPackDescriptor> packs = List.of();
    private String selectedPackId = "builtin:default";
    private String selectedTextureId = "default";
    private int currentPage;
    private FilterMode filterMode = FilterMode.ALL;
    private TextFieldWidget searchField;

    public YsmModelScreen() {
        super(Text.literal("YSM Models"));
    }

    @Override
    protected void init() {
        this.reloadPacks();
        this.rebuildWidgets();
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderDarkening(context);
        super.render(context, mouseX, mouseY, deltaTicks);

        int leftX = 18;
        int topY = 18;
        int listWidth = Math.min(220, this.width / 2 - 24);
        int rightX = leftX + listWidth + 14;
        int rightWidth = this.width - rightX - 18;

        context.drawTextWithShadow(this.textRenderer, this.title, leftX, topY, 0xFFFFFF);

        List<YsmPackDescriptor> visible = filteredPacks();
        int totalPages = Math.max(1, (visible.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal("Page " + (this.currentPage + 1) + " / " + totalPages + "    " + visible.size() + " model(s)"),
            rightX,
            topY,
            0xC0C0C0
        );

        context.fill(leftX - 4, topY + 18, leftX + listWidth, this.height - 42, 0x44202020);
        context.fill(rightX - 4, topY + 18, this.width - 18, this.height - 42, 0x44202020);

        YsmPackDescriptor current = currentPack();
        YsmActiveSelection activeSelection = YsmClientRuntime.activeSelection();
        YsmCompiledPack activePack = activeSelection == null ? null : activeSelection.compiledPack();
        YsmSourcePack sourcePack = activePack == null ? null : activePack.sourcePack();

        int y = topY + 30;
        List<Text> lines = new ArrayList<>();
        lines.add(Text.literal(current.displayName()));
        lines.add(Text.literal(current.id()));
        lines.add(Text.literal(current.sourceType() == YsmPackDescriptor.SourceType.BUILTIN ? "Builtin" : "Imported"));
        lines.add(Text.literal("Format: " + current.formatLabel()));
        lines.add(Text.literal("Texture: " + this.selectedTextureId));
        lines.add(Text.literal("Render Ready: " + (YsmRenderBridge.hasLiveModel() ? "Yes" : "No")));
        lines.add(Text.literal("Third-person: " + (current.hasMainModel() ? "Ready" : "Missing")));
        lines.add(Text.literal("First-person: " + (current.hasArmModel() ? "Compat/Arm path" : "Vanilla fallback")));
        lines.add(Text.literal("Camera/Head: GeckoLib pose-driven"));
        if (!current.details().isBlank()) {
            lines.add(Text.literal(""));
            lines.add(Text.literal(current.details()));
        }
        if (activePack != null && activePack.descriptor().id().equals(current.id()) && sourcePack != null) {
            lines.add(Text.literal(""));
            lines.add(Text.literal("Resolved texture: " + (sourcePack.hasSelectedTexture() ? sourcePack.selectedTexturePath() : "Missing")));
            lines.add(Text.literal("Main model: " + sourcePack.mainModelPath()));
            lines.add(Text.literal("Main animation: " + sourcePack.mainAnimationPath()));
        }
        lines.add(Text.literal(""));
        lines.add(Text.literal("Status: " + YsmClientRuntime.status().getCurrentStatus()));
        for (String message : YsmClientRuntime.status().recentMessages()) {
            lines.add(Text.literal(" - " + message));
        }

        for (Text line : lines) {
            context.drawWrappedTextWithShadow(this.textRenderer, line, rightX, y, rightWidth - 12, 0xD0D0D0);
            y += this.textRenderer.wrapLines(line, rightWidth - 12).size() * 9 + 4;
        }
    }

    private void reloadPacks() {
        this.packs = YsmClientRuntime.packs();
        YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info("YSM screen reload: {} pack(s) visible", this.packs.size());

        if (!this.packs.isEmpty()) {
            if (this.selectedPackId == null || this.selectedPackId.isBlank()) {
                this.selectedPackId = YsmClientRuntime.selectedPackId();
            }
            if (this.packs.stream().noneMatch(pack -> pack.id().equals(this.selectedPackId))) {
                this.selectedPackId = YsmClientRuntime.selectedPackId();
            }
            if (this.packs.stream().noneMatch(pack -> pack.id().equals(this.selectedPackId))) {
                this.selectedPackId = this.packs.getFirst().id();
            }
        }

        if (this.selectedTextureId == null || this.selectedTextureId.isBlank()) {
            this.selectedTextureId = YsmClientRuntime.selectedTextureId();
        }
        alignPageToSelection();
    }

    private void rebuildWidgets() {
        String searchText = this.searchField == null ? "" : this.searchField.getText();
        this.clearChildren();

        int leftX = 18;
        int topY = 18;
        int listWidth = Math.min(220, this.width / 2 - 24);
        int rightX = leftX + listWidth + 14;
        int rightWidth = this.width - rightX - 18;

        this.searchField = this.addDrawableChild(
            new TextFieldWidget(this.textRenderer, leftX, topY + 20, listWidth - 4, 18, Text.translatable("gui.yes_steve_model.search"))
        );
        this.searchField.setMaxLength(80);
        this.searchField.setPlaceholder(Text.translatable("gui.yes_steve_model.search"));
        this.searchField.setText(searchText);
        this.searchField.setChangedListener(value -> {
            this.currentPage = 0;
            this.rebuildWidgets();
        });
        this.setInitialFocus(this.searchField);

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.yes_steve_model.all_models"), button -> {
            this.filterMode = FilterMode.ALL;
            this.currentPage = 0;
            this.rebuildWidgets();
        }).dimensions(leftX, topY + 44, 70, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Builtin"), button -> {
            this.filterMode = FilterMode.BUILTIN;
            this.currentPage = 0;
            this.rebuildWidgets();
        }).dimensions(leftX + 76, topY + 44, 66, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Imported"), button -> {
            this.filterMode = FilterMode.IMPORTED;
            this.currentPage = 0;
            this.rebuildWidgets();
        }).dimensions(leftX + 148, topY + 44, 68, 20).build());

        List<YsmPackDescriptor> visible = filteredPacks();
        int pageCount = Math.max(1, (visible.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        this.currentPage = Math.max(0, Math.min(this.currentPage, pageCount - 1));
        int from = this.currentPage * PAGE_SIZE;
        int to = Math.min(visible.size(), from + PAGE_SIZE);

        int listY = topY + 72;
        for (int index = from; index < to; index++) {
            YsmPackDescriptor descriptor = visible.get(index);
            String label = descriptor.displayName();
            if (label.length() > 28) {
                label = label.substring(0, 27) + "…";
            }
            Text buttonText = Text.literal(descriptor.id().equals(this.selectedPackId) ? "▶ " + label : label);
            int currentIndex = index;
            this.addDrawableChild(ButtonWidget.builder(buttonText, button -> {
                this.selectedPackId = descriptor.id();
                this.selectedTextureId = descriptor.defaultTextureId();
                this.currentPage = currentIndex / PAGE_SIZE;
                this.rebuildWidgets();
            }).dimensions(leftX, listY, listWidth - 4, 20).build());
            listY += 22;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.yes_steve_model.pre_page"), button -> {
            this.currentPage = Math.max(0, this.currentPage - 1);
            this.rebuildWidgets();
        }).dimensions(leftX, this.height - 38, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.yes_steve_model.next_page"), button -> {
            this.currentPage = Math.min(pageCount - 1, this.currentPage + 1);
            this.rebuildWidgets();
        }).dimensions(leftX + 108, this.height - 38, 108, 20).build());

        YsmPackDescriptor current = currentPack();
        int actionsY = this.height - 62;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Apply"), button -> {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(
                "YSM UI: clicked Apply pack={} legacy={} texture={} root={}",
                current.id(),
                current.legacyModelId(),
                this.selectedTextureId,
                current.rootPath()
            );
            YsmClientRuntime.apply(current, this.selectedTextureId);
            this.reloadPacks();
            this.rebuildWidgets();
        }).dimensions(rightX, actionsY, 72, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), button -> {
            YsmClientRuntime.resetToDefault();
            this.reloadPacks();
            this.rebuildWidgets();
        }).dimensions(rightX + 78, actionsY, 72, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reload"), button -> {
            YsmClientRuntime.reloadRepository();
            this.reloadPacks();
            this.rebuildWidgets();
        }).dimensions(rightX + 156, actionsY, 72, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Import .ysm"), button -> {
            YsmClientRuntime.importWithFileDialog();
            this.reloadPacks();
            this.rebuildWidgets();
        }).dimensions(rightX + 234, actionsY, 96, 20).build());

        actionsY += 24;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Open Imports"), button -> YsmClientRuntime.openImportFolder())
            .dimensions(rightX, actionsY, 110, 20)
            .build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.yes_steve_model.close"), button -> this.close())
            .dimensions(rightX + 116, actionsY, 80, 20)
            .build());

        List<String> textures = current.textureIds();
        if (textures.size() > 1) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Tex <"), button -> {
                this.stepTexture(-1);
                this.rebuildWidgets();
            }).dimensions(rightX + 202, actionsY, 60, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Tex >"), button -> {
                this.stepTexture(1);
                this.rebuildWidgets();
            }).dimensions(rightX + 268, actionsY, 60, 20).build());
        }
    }

    private List<YsmPackDescriptor> filteredPacks() {
        String query = this.searchField == null ? "" : this.searchField.getText().trim().toLowerCase(Locale.ROOT);
        List<YsmPackDescriptor> visible = new ArrayList<>();
        for (YsmPackDescriptor descriptor : this.packs) {
            if (this.filterMode == FilterMode.BUILTIN && descriptor.sourceType() != YsmPackDescriptor.SourceType.BUILTIN) {
                continue;
            }
            if (this.filterMode == FilterMode.IMPORTED && descriptor.sourceType() != YsmPackDescriptor.SourceType.IMPORTED) {
                continue;
            }
            if (!query.isBlank()) {
                String haystack = (
                    descriptor.displayName() + "\n" +
                    descriptor.id() + "\n" +
                    descriptor.legacyModelId() + "\n" +
                    descriptor.details()
                ).toLowerCase(Locale.ROOT);
                if (!haystack.contains(query)) {
                    continue;
                }
            }
            visible.add(descriptor);
        }
        return visible;
    }

    private YsmPackDescriptor currentPack() {
        for (YsmPackDescriptor descriptor : this.packs) {
            if (descriptor.id().equals(this.selectedPackId)) {
                return descriptor;
            }
        }
        return this.packs.isEmpty() ? fallbackDescriptor() : this.packs.getFirst();
    }

    private void alignPageToSelection() {
        List<YsmPackDescriptor> visible = filteredPacks();
        for (int i = 0; i < visible.size(); i++) {
            if (visible.get(i).id().equals(this.selectedPackId)) {
                this.currentPage = i / PAGE_SIZE;
                return;
            }
        }
        this.currentPage = 0;
    }

    private void stepTexture(int delta) {
        List<String> textureIds = currentPack().textureIds();
        if (textureIds.isEmpty()) {
            this.selectedTextureId = currentPack().defaultTextureId();
            return;
        }
        int current = textureIds.indexOf(this.selectedTextureId);
        if (current < 0) {
            current = textureIds.indexOf(currentPack().defaultTextureId());
        }
        if (current < 0) {
            current = 0;
        }
        this.selectedTextureId = textureIds.get(Math.floorMod(current + delta, textureIds.size()));
    }

    private static YsmPackDescriptor fallbackDescriptor() {
        return new YsmPackDescriptor(
            "builtin:default",
            "default",
            "Default",
            "default",
            YsmPackDescriptor.SourceType.BUILTIN,
            Path.of("."),
            "builtin",
            "Fallback default pack",
            List.of("default"),
            true,
            true,
            true
        );
    }

    private enum FilterMode {
        ALL,
        BUILTIN,
        IMPORTED
    }
}
