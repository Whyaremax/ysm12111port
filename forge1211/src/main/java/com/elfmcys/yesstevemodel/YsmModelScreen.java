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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class YsmModelScreen extends Screen {
    private static final int PAGE_SIZE = 8;

    private List<YsmPackDescriptor> packs = List.of();
    private String selectedPackId = "builtin:default";
    private String selectedTextureId = "default";
    private int currentPage;
    private FilterMode filterMode = FilterMode.ALL;
    private EditBox searchField;

    public YsmModelScreen() {
        super(Component.literal("3DModelNow Models"));
    }

    @Override
    protected void init() {
        this.reloadPacks();
        this.rebuildWidgets();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        this.renderMenuBackground(context);
        super.render(context, mouseX, mouseY, deltaTicks);

        int leftX = 18;
        int topY = 18;
        int listWidth = Math.min(220, this.width / 2 - 24);
        int rightX = leftX + listWidth + 14;
        int rightWidth = this.width - rightX - 18;

        context.drawString(this.font, this.title, leftX, topY, 0xFFFFFF);

        List<YsmPackDescriptor> visible = filteredPacks();
        int totalPages = Math.max(1, (visible.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        context.drawString(
            this.font,
            Component.literal("Page " + (this.currentPage + 1) + " / " + totalPages + "    " + visible.size() + " model(s)"),
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
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(current.displayName()));
        lines.add(Component.literal(current.id()));
        lines.add(Component.literal(current.sourceType() == YsmPackDescriptor.SourceType.BUILTIN ? "Builtin" : "Imported"));
        lines.add(Component.literal("Format: " + current.formatLabel()));
        lines.add(Component.literal("Texture: " + this.selectedTextureId));
        lines.add(Component.literal("Render Ready: " + (YsmRenderBridge.hasLiveModel() ? "Yes" : "No")));
        lines.add(Component.literal("Third-person: " + (current.hasMainModel() ? "Ready" : "Missing")));
        lines.add(Component.literal("First-person: " + (current.hasArmModel() ? "Compat/Arm path" : "Vanilla fallback")));
        lines.add(Component.literal("Camera/Head: GeckoLib pose-driven"));
        if (!current.details().isBlank()) {
            lines.add(Component.literal(""));
            lines.add(Component.literal(current.details()));
        }
        if (activePack != null && activePack.descriptor().id().equals(current.id()) && sourcePack != null) {
            lines.add(Component.literal(""));
            lines.add(Component.literal("Resolved texture: " + (sourcePack.hasSelectedTexture() ? sourcePack.selectedTexturePath() : "Missing")));
            lines.add(Component.literal("Main model: " + sourcePack.mainModelPath()));
            lines.add(Component.literal("Main animation: " + sourcePack.mainAnimationPath()));
        }
        lines.add(Component.literal(""));
        lines.add(Component.literal("Status: " + YsmClientRuntime.status().getCurrentStatus()));
        for (String message : YsmClientRuntime.status().recentMessages()) {
            lines.add(Component.literal(" - " + message));
        }

        for (Component line : lines) {
            context.drawWordWrap(this.font, line, rightX, y, rightWidth - 12, 0xD0D0D0);
            y += this.font.split(line, rightWidth - 12).size() * 9 + 4;
        }
    }

    private void reloadPacks() {
        this.packs = YsmClientRuntime.packs();
        YesSteveModel.LOGGER.info("YSM screen reload: {} pack(s) visible", this.packs.size());

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
        this.selectedTextureId = normalizeTextureSelection(currentPack(), this.selectedTextureId);
        alignPageToSelection();
    }

    protected void rebuildWidgets() {
        String searchText = this.searchField == null ? "" : this.searchField.getValue();
        this.clearWidgets();

        int leftX = 18;
        int topY = 18;
        int listWidth = Math.min(220, this.width / 2 - 24);
        int rightX = leftX + listWidth + 14;

        this.searchField = this.addRenderableWidget(
            new EditBox(this.font, leftX, topY + 20, listWidth - 4, 18, Component.translatable("gui.yes_steve_model.search"))
        );
        this.searchField.setMaxLength(80);
        this.searchField.setHint(Component.translatable("gui.yes_steve_model.search"));
        this.searchField.setValue(searchText);
        this.searchField.setResponder(value -> {
            this.currentPage = 0;
            this.rebuildWidgets();
        });
        this.setInitialFocus(this.searchField);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.yes_steve_model.all_models"), button -> {
            this.filterMode = FilterMode.ALL;
            this.currentPage = 0;
            this.rebuildWidgets();
        }).bounds(leftX, topY + 44, 70, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Builtin"), button -> {
            this.filterMode = FilterMode.BUILTIN;
            this.currentPage = 0;
            this.rebuildWidgets();
        }).bounds(leftX + 76, topY + 44, 66, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Imported"), button -> {
            this.filterMode = FilterMode.IMPORTED;
            this.currentPage = 0;
            this.rebuildWidgets();
        }).bounds(leftX + 148, topY + 44, 68, 20).build());

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
            Component buttonText = Component.literal(descriptor.id().equals(this.selectedPackId) ? "▶ " + label : label);
            int currentIndex = index;
            this.addRenderableWidget(Button.builder(buttonText, button -> {
                this.selectedPackId = descriptor.id();
                this.selectedTextureId = normalizeTextureSelection(descriptor, descriptor.defaultTextureId());
                this.currentPage = currentIndex / PAGE_SIZE;
                this.rebuildWidgets();
            }).bounds(leftX, listY, listWidth - 4, 20).build());
            listY += 22;
        }

        this.addRenderableWidget(Button.builder(Component.translatable("gui.yes_steve_model.pre_page"), button -> {
            this.currentPage = Math.max(0, this.currentPage - 1);
            this.rebuildWidgets();
        }).bounds(leftX, this.height - 38, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.yes_steve_model.next_page"), button -> {
            this.currentPage = Math.min(pageCount - 1, this.currentPage + 1);
            this.rebuildWidgets();
        }).bounds(leftX + 108, this.height - 38, 108, 20).build());

        YsmPackDescriptor current = currentPack();
        int actionsY = this.height - 62;
        this.addRenderableWidget(Button.builder(Component.literal("Apply"), button -> {
            YesSteveModel.LOGGER.info(
                "YSM UI: clicked Apply pack={} legacy={} texture={} root={}",
                current.id(),
                current.legacyModelId(),
                this.selectedTextureId,
                current.rootPath()
            );
            YsmClientRuntime.apply(current, this.selectedTextureId);
            this.reloadPacks();
            this.rebuildWidgets();
        }).bounds(rightX, actionsY, 72, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Reset"), button -> {
            YsmClientRuntime.resetToDefault();
            this.selectedPackId = YsmClientRuntime.selectedPackId();
            this.selectedTextureId = YsmClientRuntime.selectedTextureId();
            this.reloadPacks();
            this.rebuildWidgets();
        }).bounds(rightX + 78, actionsY, 72, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Reload"), button -> {
            YsmClientRuntime.reloadRepository();
            this.reloadPacks();
            this.rebuildWidgets();
        }).bounds(rightX + 156, actionsY, 72, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Import .ysm"), button -> {
            YsmClientRuntime.importWithFileDialog();
            this.reloadPacks();
            this.rebuildWidgets();
        }).bounds(rightX + 234, actionsY, 96, 20).build());

        actionsY += 24;
        this.addRenderableWidget(Button.builder(Component.literal("Open Imports"), button -> YsmClientRuntime.openImportFolder())
            .bounds(rightX, actionsY, 110, 20)
            .build());
        this.addRenderableWidget(Button.builder(Component.literal("Textures..."), button -> this.openTextureSelection(current))
            .bounds(rightX + 116, actionsY, 88, 20)
            .build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.yes_steve_model.close"), button -> this.onClose())
            .bounds(rightX + 210, actionsY, 80, 20)
            .build());

        List<String> textures = current.textureIds();
        if (textures.size() > 1) {
            this.addRenderableWidget(Button.builder(Component.literal("Tex <"), button -> {
                this.stepTexture(-1);
                this.rebuildWidgets();
            }).bounds(rightX + 296, actionsY, 60, 20).build());
            this.addRenderableWidget(Button.builder(Component.literal("Tex >"), button -> {
                this.stepTexture(1);
                this.rebuildWidgets();
            }).bounds(rightX + 362, actionsY, 60, 20).build());
        }
    }

    private List<YsmPackDescriptor> filteredPacks() {
        String query = this.searchField == null ? "" : this.searchField.getValue().trim().toLowerCase(Locale.ROOT);
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
        YsmPackDescriptor descriptor = currentPack();
        List<String> textureIds = descriptor.textureIds();
        if (textureIds.isEmpty()) {
            this.selectedTextureId = descriptor.defaultTextureId();
            return;
        }
        int current = textureIds.indexOf(this.selectedTextureId);
        if (current < 0) {
            current = textureIds.indexOf(descriptor.defaultTextureId());
        }
        if (current < 0) {
            current = 0;
        }
        this.selectedTextureId = textureIds.get(Math.floorMod(current + delta, textureIds.size()));
    }

    private void openTextureSelection(YsmPackDescriptor descriptor) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return;
        }

        client.setScreen(new YsmTextureSelectionScreen(
            this,
            descriptor,
            this.selectedTextureId,
            textureId -> this.selectedTextureId = normalizeTextureSelection(descriptor, textureId)
        ));
    }

    private static String normalizeTextureSelection(YsmPackDescriptor descriptor, String textureId) {
        List<String> textureIds = descriptor.textureIds();
        if (textureId != null && textureIds.contains(textureId)) {
            return textureId;
        }
        if (textureIds.contains(descriptor.defaultTextureId())) {
            return descriptor.defaultTextureId();
        }
        return textureIds.isEmpty() ? "default" : textureIds.getFirst();
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
