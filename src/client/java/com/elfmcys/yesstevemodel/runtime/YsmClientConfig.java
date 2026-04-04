package com.elfmcys.yesstevemodel.runtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import net.fabricmc.loader.api.FabricLoader;

public final class YsmClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path path;
    private Data data;

    public YsmClientConfig() {
        Path dir = FabricLoader.getInstance().getConfigDir().resolve("yes_steve_model");
        this.path = dir.resolve("client-runtime.json");
        this.data = new Data();
    }

    public synchronized void load() throws IOException {
        Files.createDirectories(this.path.getParent());
        if (!Files.exists(this.path)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(this.path)) {
            Data loaded = GSON.fromJson(reader, Data.class);
            this.data = loaded == null ? new Data() : loaded.normalized();
        } catch (JsonSyntaxException exception) {
            this.data = new Data();
            save();
        }
    }

    public synchronized void save() throws IOException {
        Files.createDirectories(this.path.getParent());
        try (Writer writer = Files.newBufferedWriter(this.path)) {
            GSON.toJson(this.data.normalized(), writer);
        }
    }

    public synchronized String getSelectedPackId() {
        return this.data.selectedPackId;
    }

    public synchronized void setSelectedPackId(String selectedPackId) {
        this.data.selectedPackId = Objects.requireNonNullElse(selectedPackId, "builtin:default");
    }

    public synchronized String getSelectedTextureId() {
        return this.data.selectedTextureId;
    }

    public synchronized void setSelectedTextureId(String selectedTextureId) {
        this.data.selectedTextureId = Objects.requireNonNullElse(selectedTextureId, "default");
    }

    public synchronized String getLastImportDirectory() {
        return this.data.lastImportDirectory;
    }

    public synchronized void setLastImportDirectory(String lastImportDirectory) {
        this.data.lastImportDirectory = lastImportDirectory;
    }

    public Path getCacheRoot() {
        return this.path.getParent().resolve("imported");
    }

    public Path getImportsRoot() {
        return this.path.getParent();
    }

    private static final class Data {
        private String selectedPackId = "builtin:default";
        private String selectedTextureId = "default";
        private String lastImportDirectory;

        private Data normalized() {
            if (this.selectedPackId == null || this.selectedPackId.isBlank()) {
                this.selectedPackId = "builtin:default";
            }
            if (this.selectedTextureId == null || this.selectedTextureId.isBlank()) {
                this.selectedTextureId = "default";
            }
            return this;
        }
    }
}
