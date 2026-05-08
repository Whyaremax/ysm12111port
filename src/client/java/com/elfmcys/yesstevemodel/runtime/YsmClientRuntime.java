package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YesSteveModel;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

public final class YsmClientRuntime {
    private static final YsmClientConfig CONFIG = new YsmClientConfig();
    private static final YsmStatusService STATUS = new YsmStatusService();
    private static final YsmPackRepository REPOSITORY = new YsmPackRepository(CONFIG);
    private static final YsmSelectionService SELECTION = new YsmSelectionService(CONFIG, REPOSITORY, STATUS);

    private static boolean initialized;
    private static int appliedPlayerId = Integer.MIN_VALUE;

    private YsmClientRuntime() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            CONFIG.load();
            syncLooseImports();
            REPOSITORY.reload();
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info("YSM runtime initialized with {} pack(s)", REPOSITORY.all().size());
            STATUS.setStatus("YSM client runtime loaded " + REPOSITORY.all().size() + " pack(s)");
            initialized = true;
        } catch (Throwable throwable) {
            STATUS.setStatus("YSM client runtime failed to initialize");
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("Failed to initialize YSM client runtime", throwable);
        }
    }

    public static YsmStatusService status() {
        return STATUS;
    }

    public static List<YsmPackDescriptor> packs() {
        initialize();
        return REPOSITORY.ordered();
    }

    public static String selectedPackId() {
        initialize();
        return SELECTION.getSelectedPackId();
    }

    public static String selectedTextureId() {
        initialize();
        return SELECTION.getSelectedTextureId();
    }

    public static List<String> currentTextureOptions() {
        initialize();
        return REPOSITORY.get(SELECTION.getSelectedPackId()).map(YsmPackDescriptor::textureIds).orElse(List.of("default"));
    }

    public static YsmActiveSelection activeSelection() {
        initialize();
        return SELECTION.activeSelection().orElse(null);
    }

    public static boolean hasRuntimeAppliedSelection() {
        initialize();
        return SELECTION.hasRuntimeAppliedSelection();
    }

    public static void apply(YsmPackDescriptor descriptor) {
        apply(descriptor, descriptor.defaultTextureId());
    }

    public static void apply(YsmPackDescriptor descriptor, String textureId) {
        initialize();
        try {
            SELECTION.select(descriptor, textureId);
        } catch (IOException exception) {
            STATUS.setStatus("Failed to save selection: " + exception.getMessage());
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("Failed to save YSM selection", exception);
        }
    }

    public static void resetToDefault() {
        initialize();
        try {
            SELECTION.resetToDefault();
        } catch (IOException exception) {
            STATUS.setStatus("Failed to reset selection: " + exception.getMessage());
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("Failed to reset YSM selection", exception);
        }
    }

    public static void reloadRepository() {
        initialize();
        try {
            syncLooseImports();
            REPOSITORY.reload();
            SELECTION.reapplyStoredSelection();
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info("YSM runtime reload complete: {} pack(s)", REPOSITORY.all().size());
            STATUS.setStatus("Reloaded " + REPOSITORY.all().size() + " pack(s)");
        } catch (IOException exception) {
            STATUS.setStatus("Reload failed: " + exception.getMessage());
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("Failed to reload YSM packs", exception);
        }
    }

    public static void openImportFolder() {
        initialize();
        try {
            Path importsRoot = CONFIG.getCacheRoot();
            java.nio.file.Files.createDirectories(importsRoot);
            Util.getOperatingSystem().open(importsRoot.toFile());
            STATUS.setStatus("Opened import folder. Drop .ysm, .zip, or raw model folders there, then press Reload.");
        } catch (IOException exception) {
            STATUS.setStatus("Failed to open import folder: " + exception.getMessage());
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("Failed to open YSM import folder", exception);
        }
    }

    public static void importWithFileDialog() {
        initialize();
        Path source = chooseYsmFile();
        if (source == null) {
            return;
        }

        CONFIG.setLastImportDirectory(source.getParent().toString());
        try {
            CONFIG.save();
            YsmPackDescriptor descriptor = YsmPythonImporter.importPack(source, CONFIG.getCacheRoot());
            REPOSITORY.reload();
            SELECTION.select(descriptor, descriptor.defaultTextureId());
            STATUS.setStatus("Imported " + descriptor.displayName());
        } catch (Throwable throwable) {
            STATUS.setStatus("Import failed: " + throwable.getMessage());
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("Failed to import YSM pack {}", source, throwable);
        }
    }

    public static void tick(MinecraftClient client) {
        initialize();
        if (client == null || client.player == null || client.getTextureManager() == null) {
            appliedPlayerId = Integer.MIN_VALUE;
            return;
        }

        if (client.player.getId() == appliedPlayerId) {
            return;
        }

        SELECTION.reapplyStoredSelection();
        appliedPlayerId = client.player.getId();
    }

    private static Path chooseYsmFile() {
        try {
            FileDialog dialog = new FileDialog((Frame) null, "Import YSM", FileDialog.LOAD);
            String lastImportDirectory = CONFIG.getLastImportDirectory();
            if (lastImportDirectory != null && !lastImportDirectory.isBlank()) {
                dialog.setDirectory(lastImportDirectory);
            }
            dialog.setFile("*.ysm;*.zip");
            dialog.setFilenameFilter((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".ysm") || lower.endsWith(".zip");
            });
            dialog.setVisible(true);
            if (dialog.getFile() == null) {
                return null;
            }
            return Path.of(dialog.getDirectory(), dialog.getFile());
        } catch (Throwable throwable) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.debug("Native file chooser unavailable", throwable);
            MinecraftClient client = MinecraftClient.getInstance();
            try {
                Path importsRoot = CONFIG.getCacheRoot();
                java.nio.file.Files.createDirectories(importsRoot);
                Util.getOperatingSystem().open(importsRoot.toFile());
                STATUS.setStatus("Import picker unavailable. Opened imports folder; drop .ysm, .zip, or raw model folders there and press Reload.");
            } catch (Throwable openThrowable) {
                YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.debug("Failed to open imports root after picker fallback", openThrowable);
            }
            if (client != null && client.player != null) {
                client.player.sendMessage(
                    net.minecraft.text.Text.literal("YSM import picker is unavailable here. The imports folder was opened; drop .ysm, .zip, or raw model folders there and press Reload."),
                    false
                );
            }
            return null;
        }
    }

    private static void syncLooseImports() throws IOException {
        try {
            YsmPythonImporter.SyncResult result = YsmPythonImporter.syncDroppedPacks(CONFIG.getCacheRoot());
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(
                "YSM loose import sync: imported={} skipped={} failed={}",
                result.imported().size(),
                result.skipped().size(),
                result.failed().size()
            );
            if (!result.imported().isEmpty()) {
                YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info("YSM loose import sync imported: {}", result.imported());
                STATUS.pushMessage("Imported " + result.imported().size() + " dropped pack(s): " + String.join(", ", result.imported()));
            }
            if (!result.failed().isEmpty()) {
                for (String failure : result.failed()) {
                    YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("YSM loose import failure: {}", failure);
                    STATUS.pushMessage("Import failed: " + failure);
                }
                STATUS.setStatus("Some dropped YSM files failed to import");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("YSM import was interrupted", exception);
        }
    }
}
