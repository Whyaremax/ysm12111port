package com.elfmcys.yesstevemodel.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.elfmcys.yesstevemodel.YesSteveModel;
import com.threedmodelnow.core.ThreeDModelNow;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public final class YsmPackRepository {
    private static final Pattern FORMAT_PATTERN = Pattern.compile("<format>\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern NAME_PATTERN = Pattern.compile("<name>\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TEXTURE_PATTERN = Pattern.compile("<texture>\\s+(\\S+)", Pattern.CASE_INSENSITIVE);

    private final YsmClientConfig config;
    private Map<String, YsmPackDescriptor> descriptors = Map.of();

    public YsmPackRepository(YsmClientConfig config) {
        this.config = config;
    }

    public synchronized void reload() throws IOException {
        Map<String, YsmPackDescriptor> scanned = new LinkedHashMap<>();
        scanBuiltins(scanned);
        scanImported(scanned);
        this.descriptors = Map.copyOf(scanned);
    }

    public synchronized Collection<YsmPackDescriptor> all() {
        return this.descriptors.values();
    }

    public synchronized List<YsmPackDescriptor> ordered() {
        List<YsmPackDescriptor> ordered = new ArrayList<>(this.descriptors.values());
        ordered.sort(
            Comparator.comparing(YsmPackDescriptor::sourceType)
                .thenComparing(YsmPackDescriptor::displayName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(YsmPackDescriptor::id)
        );
        return ordered;
    }

    public synchronized Optional<YsmPackDescriptor> get(String id) {
        return Optional.ofNullable(this.descriptors.get(id));
    }

    public synchronized YsmPackDescriptor defaultPack() {
        return this.descriptors.getOrDefault(
            "builtin:default",
            new YsmPackDescriptor(
                "builtin:default",
                "default",
                "Default",
                "default",
                YsmPackDescriptor.SourceType.BUILTIN,
                this.config.getImportsRoot(),
                "builtin",
                "Bundled default model",
                List.of("default"),
                true,
                true,
                true
            )
        );
    }

    private static void scanBuiltins(Map<String, YsmPackDescriptor> scanned) throws IOException {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(ThreeDModelNow.YSM_COMPAT_MOD_ID);
        if (modContainer.isEmpty()) {
            modContainer = FabricLoader.getInstance().getModContainer(YesSteveModel.OoO0O0oO00O0o0OOOOoOOooo);
        }
        if (modContainer.isEmpty()) {
            return;
        }

        Optional<Path> builtinRoot = modContainer.get().findPath("assets/yes_steve_model/builtin");
        if (builtinRoot.isEmpty() || !Files.exists(builtinRoot.get())) {
            return;
        }

        try (var stream = Files.walk(builtinRoot.get())) {
            stream.filter(path -> Files.isRegularFile(path) && path.getFileName().toString().equals("ysm.json")).forEach(manifest -> {
                Path path = manifest.getParent();
                if (!Files.exists(manifest)) {
                    return;
                }

                try (Reader reader = Files.newBufferedReader(manifest)) {
                    JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                    JsonObject properties = root.has("properties") ? root.getAsJsonObject("properties") : new JsonObject();
                    JsonObject files = root.has("files") ? root.getAsJsonObject("files") : new JsonObject();
                    JsonObject player = files.has("player") ? files.getAsJsonObject("player") : new JsonObject();
                    JsonObject models = player.has("model") ? player.getAsJsonObject("model") : new JsonObject();
                    JsonObject animations = player.has("animation") ? player.getAsJsonObject("animation") : new JsonObject();
                    Path relative = builtinRoot.get().relativize(path);
                    String legacyModelId = relative.toString().replace('\\', '/');
                    String name = getString(root.get("name"), path.getFileName().toString());
                    String tips = getString(root.get("description"), "Bundled model");
                    String defaultTexture = sanitizeTexture(getString(properties.get("default_texture"), "default"));
                    List<String> textureIds = parseTextureIds(path, player, defaultTexture);
                    scanned.put(
                        "builtin:" + legacyModelId,
                        new YsmPackDescriptor(
                            "builtin:" + legacyModelId,
                            legacyModelId,
                            name,
                            defaultTexture,
                            YsmPackDescriptor.SourceType.BUILTIN,
                            path,
                            "builtin",
                            tips,
                            textureIds,
                            exists(path, getString(models.get("main"), null)),
                            exists(path, getString(models.get("arm"), null)),
                            exists(path, getString(animations.get("fp_arm"), null)) || exists(path, getString(animations.get("arm"), null))
                        )
                    );
                } catch (Throwable throwable) {
                    YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("Skipped builtin YSM pack {}", path, throwable);
                }
            });
        }
    }

    private void scanImported(Map<String, YsmPackDescriptor> scanned) throws IOException {
        scanImportedRoot(scanned, this.config.getLegacyCacheRoot(), false);
        scanImportedRoot(scanned, this.config.getCacheRoot(), true);
    }

    private void scanImportedRoot(Map<String, YsmPackDescriptor> scanned, Path importedRoot, boolean create) throws IOException {
        if (create) {
            Files.createDirectories(importedRoot);
        }
        if (!Files.isDirectory(importedRoot)) {
            return;
        }
        try (var stream = Files.list(importedRoot)) {
            stream.filter(Files::isDirectory).forEach(path -> {
                try {
                    importedDescriptor(path).ifPresent(descriptor -> scanned.put(descriptor.id(), descriptor));
                } catch (Throwable throwable) {
                    YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn("Skipped imported YSM pack {}", path, throwable);
                }
            });
        }
    }

    private Optional<YsmPackDescriptor> importedDescriptor(Path path) throws IOException {
        Path descriptorPath = path.resolve("descriptor.json");
        if (Files.exists(descriptorPath)) {
            try (Reader reader = Files.newBufferedReader(descriptorPath)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                return Optional.of(
                    new YsmPackDescriptor(
                        getString(root.get("id"), "imported:" + path.getFileName()),
                        getString(root.get("legacyModelId"), path.getFileName().toString()),
                        getString(root.get("displayName"), path.getFileName().toString()),
                        getString(root.get("defaultTextureId"), "default"),
                        YsmPackDescriptor.SourceType.IMPORTED,
                        path,
                        getString(root.get("formatLabel"), "imported"),
                        getString(root.get("details"), "Imported pack"),
                        parseTextureIds(path, path.resolve("ysm.json"), getString(root.get("defaultTextureId"), "default")),
                        parseManifest(path.resolve("ysm.json")).hasMainModel(),
                        parseManifest(path.resolve("ysm.json")).hasArmModel(),
                        parseManifest(path.resolve("ysm.json")).hasFpArmAnimation()
                    )
                );
            }
        }

        Path ysmJson = path.resolve("ysm.json");
        if (Files.exists(ysmJson)) {
            try (Reader reader = Files.newBufferedReader(ysmJson)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject metadata = root.has("metadata") ? root.getAsJsonObject("metadata") : new JsonObject();
                JsonObject properties = root.has("properties") ? root.getAsJsonObject("properties") : new JsonObject();
                String legacyModelId = path.getFileName().toString();
                ManifestState manifestState = parseManifest(root, path);
                return Optional.of(
                    new YsmPackDescriptor(
                        "imported:" + legacyModelId,
                        legacyModelId,
                        getString(metadata.get("name"), legacyModelId),
                        getString(properties.get("default_texture"), "default"),
                        YsmPackDescriptor.SourceType.IMPORTED,
                        path,
                        "imported",
                        getString(metadata.get("tips"), "Imported pack"),
                        parseTextureIds(path, root, getString(properties.get("default_texture"), "default")),
                        manifestState.hasMainModel(),
                        manifestState.hasArmModel(),
                        manifestState.hasFpArmAnimation()
                    )
                );
            }
        }

        Path propertyFile = path.resolve("property.txt");
        if (!Files.exists(propertyFile)) {
            return Optional.empty();
        }

        String propertyText = Files.readString(propertyFile);
        String displayName = firstMatch(NAME_PATTERN, propertyText).orElse(path.getFileName().toString());
        String format = firstMatch(FORMAT_PATTERN, propertyText).map(value -> "format " + value).orElse("imported");
        String texture = firstMatch(TEXTURE_PATTERN, propertyText).orElse("default");
        String legacyModelId = path.getFileName().toString().replaceFirst("^imported_", "");
        return Optional.of(
            new YsmPackDescriptor(
                "imported:" + legacyModelId,
                legacyModelId,
                displayName,
                sanitizeTexture(texture),
                YsmPackDescriptor.SourceType.IMPORTED,
                path,
                format,
                "Imported extracted pack",
                scanTextureIds(path, sanitizeTexture(texture)),
                Files.exists(path.resolve("models/main.json")),
                Files.exists(path.resolve("models/arm.json")),
                Files.exists(path.resolve("animations/fp.arm.animation.json")) || Files.exists(path.resolve("animations/arm.animation.json"))
            )
        );
    }

    private static ManifestState parseManifest(Path ysmJson) throws IOException {
        if (!Files.exists(ysmJson)) {
            return ManifestState.EMPTY;
        }
        try (Reader reader = Files.newBufferedReader(ysmJson)) {
            return parseManifest(JsonParser.parseReader(reader).getAsJsonObject(), ysmJson.getParent());
        }
    }

    private static ManifestState parseManifest(JsonObject root, Path packRoot) {
        JsonObject files = root.has("files") ? root.getAsJsonObject("files") : new JsonObject();
        JsonObject player = files.has("player") ? files.getAsJsonObject("player") : new JsonObject();
        JsonObject model = player.has("model") ? player.getAsJsonObject("model") : new JsonObject();
        JsonObject animation = player.has("animation") ? player.getAsJsonObject("animation") : new JsonObject();
        return new ManifestState(
            exists(packRoot, getString(model.get("main"), null)),
            exists(packRoot, getString(model.get("arm"), null)),
            exists(packRoot, getString(animation.get("fp_arm"), null)) || exists(packRoot, getString(animation.get("arm"), null))
        );
    }

    private static List<String> parseTextureIds(Path packRoot, Path ysmJson, String defaultTexture) throws IOException {
        if (!Files.exists(ysmJson)) {
            return scanTextureIds(packRoot, defaultTexture);
        }
        try (Reader reader = Files.newBufferedReader(ysmJson)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            return parseTextureIds(packRoot, root, defaultTexture);
        }
    }

    private static List<String> parseTextureIds(Path packRoot, JsonObject root, String defaultTexture) {
        JsonObject files = root.has("files") ? root.getAsJsonObject("files") : new JsonObject();
        JsonObject player = files.has("player") ? files.getAsJsonObject("player") : new JsonObject();
        return parseTextureIdsFromPlayer(packRoot, player, defaultTexture);
    }

    private static List<String> parseTextureIdsFromPlayer(Path packRoot, JsonObject player, String defaultTexture) {
        Set<String> textures = new LinkedHashSet<>();
        if (player.has("texture") && player.get("texture").isJsonArray()) {
            for (JsonElement element : player.getAsJsonArray("texture")) {
                textures.add(textureIdFromPath(getString(element, defaultTexture)));
            }
        }
        textures.addAll(scanTextureIds(packRoot, defaultTexture));
        textures.add(sanitizeTexture(defaultTexture));
        textures.removeIf(String::isBlank);
        return List.copyOf(textures);
    }

    private static List<String> scanTextureIds(Path packRoot, String defaultTexture) {
        Set<String> textureIds = new LinkedHashSet<>();
        Path textureRoot = packRoot.resolve("textures");
        if (Files.exists(textureRoot)) {
            try (var stream = Files.walk(textureRoot)) {
                stream.filter(Files::isRegularFile).forEach(path -> {
                    String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
                    if (!fileName.endsWith(".png")) {
                        return;
                    }
                    Path relative = textureRoot.relativize(path);
                    String normalized = relative.toString().replace('\\', '/');
                    textureIds.add(textureIdFromPath(normalized));
                });
            } catch (IOException exception) {
                YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.debug("Failed to scan texture ids under {}", textureRoot, exception);
            }
        }
        textureIds.add(sanitizeTexture(defaultTexture));
        return List.copyOf(textureIds);
    }

    private static String textureIdFromPath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return "default";
        }
        String normalized = rawPath.replace('\\', '/');
        if (normalized.startsWith("textures/")) {
            normalized = normalized.substring("textures/".length());
        }
        if (normalized.endsWith(".png")) {
            normalized = normalized.substring(0, normalized.length() - 4);
        }
        return sanitizeTexture(normalized);
    }

    private static boolean exists(Path root, String relativePath) {
        return relativePath != null && !relativePath.isBlank() && Files.exists(root.resolve(relativePath));
    }

    private static Optional<String> firstMatch(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? Optional.ofNullable(matcher.group(1)).map(String::trim) : Optional.empty();
    }

    private static String sanitizeTexture(String texture) {
        if (texture == null || texture.isBlank()) {
            return "default";
        }
        String normalized = texture.replace('\\', '/').toLowerCase(Locale.ROOT);
        if (normalized.startsWith("textures/")) {
            normalized = normalized.substring("textures/".length());
        }
        if (normalized.endsWith(".png")) {
            normalized = normalized.substring(0, normalized.length() - 4);
        }
        return normalized.replaceAll("[^a-z0-9._/-]+", "_");
    }

    private static String getString(JsonElement element, String fallback) {
        return element != null && !element.isJsonNull() ? element.getAsString() : fallback;
    }

    private record ManifestState(boolean hasMainModel, boolean hasArmModel, boolean hasFpArmAnimation) {
        private static final ManifestState EMPTY = new ManifestState(false, false, false);
    }
}
