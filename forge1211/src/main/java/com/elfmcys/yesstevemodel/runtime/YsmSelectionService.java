package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YesSteveModel;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public final class YsmSelectionService {
    private final YsmClientConfig config;
    private final YsmPackRepository repository;
    private final YsmStatusService statusService;
    private final YsmPackCompiler compiler = new YsmPackCompiler();
    private final YsmGeoPackCompiler geoCompiler = new YsmGeoPackCompiler();
    private volatile YsmActiveSelection activeSelection;
    private volatile YsmActiveSelection lastGoodSelection;

    public YsmSelectionService(YsmClientConfig config, YsmPackRepository repository, YsmStatusService statusService) {
        this.config = config;
        this.repository = repository;
        this.statusService = statusService;
    }

    public String getSelectedPackId() {
        return this.config.getSelectedPackId();
    }

    public String getSelectedTextureId() {
        return this.config.getSelectedTextureId();
    }

    public Optional<YsmActiveSelection> activeSelection() {
        return Optional.ofNullable(this.activeSelection);
    }

    public boolean hasRuntimeAppliedSelection() {
        return this.activeSelection != null && this.activeSelection.runtimeApplied();
    }

    public void select(YsmPackDescriptor descriptor, String textureId) throws IOException {
        YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(
            "YSM select start: pack={} legacy={} requestedTexture={} root={}",
            descriptor.id(),
            descriptor.legacyModelId(),
            textureId,
            descriptor.rootPath()
        );

        YsmCompiledPack compiledPack = this.compiler.compile(descriptor, textureId);
        YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(
            "YSM select compiled: pack={} selectedTexture={} thirdPerson={} firstPerson={} warnings={}",
            descriptor.id(),
            compiledPack.selectedTextureId(),
            compiledPack.renderableThirdPerson(),
            compiledPack.renderableFirstPerson(),
            compiledPack.warnings().size()
        );

        if (compiledPack.sourcePack() != null) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(
                "YSM source pack: mainModel={} armModel={} mainAnim={} armAnim={} fpArm={} texture={} root={}",
                compiledPack.sourcePack().mainModelPath(),
                compiledPack.sourcePack().armModelPath(),
                compiledPack.sourcePack().mainAnimationPath(),
                compiledPack.sourcePack().armAnimationPath(),
                compiledPack.sourcePack().fpArmAnimationPath(),
                compiledPack.sourcePack().selectedTexturePath(),
                compiledPack.sourcePack().rootPath()
            );
        }

        for (String warning : compiledPack.warnings()) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("YSM compile warning for {}: {}", descriptor.id(), warning);
        }

        if (!compiledPack.renderableThirdPerson()) {
            this.statusService.setStatus("Pack " + descriptor.displayName() + " is missing a player body model");
            fallbackToLastGood();
            return;
        }

        YsmGeoPack geoPack;
        try {
            geoPack = this.geoCompiler.compile(compiledPack);
        } catch (IOException exception) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(
                "YSM runtime apply failed for pack={} texture={}: {}",
                descriptor.id(),
                compiledPack.selectedTextureId(),
                exception.toString()
            );
            this.statusService.setStatus("Failed to apply " + descriptor.displayName() + ": " + exception.getMessage());
            fallbackToLastGood();
            return;
        }

        YsmRenderStateStore.install(geoPack);
        this.activeSelection = new YsmActiveSelection(compiledPack, geoPack, true);
        this.lastGoodSelection = this.activeSelection;

        this.config.setSelectedPackId(descriptor.id());
        this.config.setSelectedTextureId(compiledPack.selectedTextureId());
        this.config.save();

        if (compiledPack.warnings().isEmpty()) {
            this.statusService.setStatus("Applied " + descriptor.displayName() + " [" + compiledPack.selectedTextureId() + "]");
        } else {
            this.statusService.setStatus("Applied " + descriptor.displayName() + " [" + compiledPack.selectedTextureId() + "] with warnings");
            compiledPack.warnings().forEach(this.statusService::pushMessage);
        }

        YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(
            "YSM runtime apply succeeded: pack={} texture={} model={} animation={} renderReady=true",
            descriptor.id(),
            compiledPack.selectedTextureId(),
            geoPack.modelResource(),
            geoPack.animationResource()
        );
        YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(
            "YSM select done: pack={} texture={} runtimeApplied=true",
            descriptor.id(),
            compiledPack.selectedTextureId()
        );
    }

    public void select(YsmPackDescriptor descriptor) throws IOException {
        select(descriptor, descriptor.defaultTextureId());
    }

    public void resetToDefault() throws IOException {
        YsmPackDescriptor descriptor = this.repository.defaultPack();
        select(descriptor);
    }

    public void reapplyStoredSelection() {
        Optional<YsmPackDescriptor> selected = this.repository.get(this.config.getSelectedPackId());
        selected.ifPresentOrElse(
            descriptor -> {
                try {
                    select(descriptor, this.config.getSelectedTextureId());
                } catch (IOException exception) {
                    this.statusService.setStatus("Failed to reapply stored selection");
                    YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("Failed to reapply stored selection", exception);
                    fallbackToLastGood();
                }
            },
            () -> {
                try {
                    resetToDefault();
                } catch (IOException exception) {
                    this.statusService.setStatus("Failed to restore default pack");
                    YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("Failed to restore default YSM pack", exception);
                    this.activeSelection = null;
                    YsmRenderStateStore.clear();
                }
            }
        );
    }

    private void fallbackToLastGood() {
        if (this.lastGoodSelection == null) {
            YsmRenderStateStore.clear();
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("YSM fallback requested but there is no last good selection");
            return;
        }

        this.activeSelection = this.lastGoodSelection;
        YsmRenderStateStore.install(this.lastGoodSelection.geoPack());
        YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(
            "YSM fallback to last good selection: pack={} texture={}",
            this.lastGoodSelection.compiledPack().descriptor().id(),
            this.lastGoodSelection.compiledPack().selectedTextureId()
        );
        this.statusService.pushMessage("Kept previous working selection: " + this.lastGoodSelection.compiledPack().descriptor().displayName());
    }
}
