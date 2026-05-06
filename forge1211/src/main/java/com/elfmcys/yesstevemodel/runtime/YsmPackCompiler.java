package com.elfmcys.yesstevemodel.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;

public final class YsmPackCompiler {
    public YsmCompiledPack compile(YsmPackDescriptor descriptor, String requestedTextureId) throws IOException {
        List<String> warnings = new ArrayList<>();
        String resolvedTexture = resolveTexture(descriptor, requestedTextureId, warnings);
        YsmSourcePack sourcePack = loadSourcePack(descriptor, resolvedTexture, warnings);
        resolvedTexture = sourcePack.selectedTextureId();
        boolean thirdPerson = sourcePack.hasMainModel();
        boolean firstPerson = sourcePack.hasArmModel();

        if (!thirdPerson) {
            warnings.add("Missing player main model");
        }
        if (!firstPerson) {
            warnings.add("Missing player arm model, first-person falls back to vanilla");
        } else if (!sourcePack.hasFpArmAnimation() && !sourcePack.hasArmAnimation()) {
            warnings.add("Missing first-person arm animation, first-person falls back to static arm model");
        } else if (!sourcePack.hasFpArmAnimation()) {
            warnings.add("Missing fp_arm animation, falling back to arm animation");
        }
        if (!sourcePack.hasSelectedTexture()) {
            warnings.add("Missing selected texture asset");
        }
        if (!sourcePack.hasMainAnimation()) {
            warnings.add("Missing main animation");
        }

        return new YsmCompiledPack(descriptor, sourcePack, resolvedTexture, thirdPerson, firstPerson, warnings);
    }

    private static String resolveTexture(YsmPackDescriptor descriptor, String requestedTextureId, List<String> warnings) {
        List<String> textures = descriptor.textureIds();
        if (requestedTextureId != null && textures.contains(requestedTextureId)) {
            return requestedTextureId;
        }
        if (requestedTextureId != null && !requestedTextureId.isBlank()) {
            warnings.add("Texture \"" + requestedTextureId + "\" is unavailable for this pack");
        }
        if (textures.contains(descriptor.defaultTextureId())) {
            return descriptor.defaultTextureId();
        }
        return textures.isEmpty() ? "default" : textures.get(0);
    }

    private static YsmSourcePack loadSourcePack(YsmPackDescriptor descriptor, String resolvedTexture, List<String> warnings) throws IOException {
        JsonObject manifest = readJsonIfExists(descriptor.rootPath().resolve("ysm.json"));
        JsonObject files = child(manifest, "files");
        JsonObject player = child(files, "player");
        JsonObject model = child(player, "model");
        JsonObject animation = child(player, "animation");

        Path root = descriptor.rootPath();
        Path mainModelPath = resolveAsset(root, getString(model, "main"), "models/main.json");
        Path armModelPath = resolveAsset(root, getString(model, "arm"), "models/arm.json");
        Path mainAnimationPath = resolveAsset(root, getString(animation, "main"), "animations/main.animation.json");
        Path armAnimationPath = resolveAsset(root, getString(animation, "arm"), "animations/arm.animation.json");
        Path fpArmAnimationPath = resolveAsset(root, getString(animation, "fp_arm"), "animations/fp.arm.animation.json");

        JsonObject mainModel = readJsonIfExists(mainModelPath);
        JsonObject armModel = readJsonIfExists(armModelPath);
        JsonObject mainAnimation = readJsonIfExists(mainAnimationPath);
        JsonObject armAnimation = readJsonIfExists(armAnimationPath);
        JsonObject fpArmAnimation = readJsonIfExists(fpArmAnimationPath);

        Map<String, Path> textures = resolveTextures(descriptor, player, warnings);
        String selectedTextureId = resolvedTexture;
        Path selectedTexturePath = textures.get(selectedTextureId);
        if (selectedTexturePath == null && !textures.isEmpty()) {
            Map.Entry<String, Path> fallback = textures.entrySet().iterator().next();
            selectedTextureId = fallback.getKey();
            selectedTexturePath = fallback.getValue();
        }

        return new YsmSourcePack(
            root,
            manifest,
            mainModelPath,
            mainModel,
            armModelPath,
            armModel,
            mainAnimationPath,
            mainAnimation,
            armAnimationPath,
            armAnimation,
            fpArmAnimationPath,
            fpArmAnimation,
            textures,
            selectedTextureId,
            selectedTexturePath
        );
    }

    private static Map<String, Path> resolveTextures(YsmPackDescriptor descriptor, JsonObject player, List<String> warnings) {
        Map<String, Path> resolved = new LinkedHashMap<>();
        JsonElement textureElement = player == null ? null : player.get("texture");
        if (textureElement != null) {
            if (textureElement.isJsonArray()) {
                for (JsonElement element : textureElement.getAsJsonArray()) {
                    if (element == null || element.isJsonNull()) {
                        continue;
                    }
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                        putTexture(resolved, descriptor.rootPath(), element.getAsString());
                        continue;
                    }
                    if (!element.isJsonObject()) {
                        continue;
                    }
                    JsonObject object = element.getAsJsonObject();
                    String texturePath = getString(object, "uv");
                    if (texturePath == null) {
                        texturePath = getString(object, "texture");
                    }
                    putTexture(resolved, descriptor.rootPath(), texturePath);
                }
            } else if (textureElement.isJsonPrimitive() && textureElement.getAsJsonPrimitive().isString()) {
                putTexture(resolved, descriptor.rootPath(), textureElement.getAsString());
            }
        }

        for (String textureId : descriptor.textureIds()) {
            resolved.computeIfAbsent(textureId, key -> conventionalTexture(descriptor.rootPath(), key));
        }
        resolved.computeIfAbsent(descriptor.defaultTextureId(), key -> conventionalTexture(descriptor.rootPath(), key));
        resolved.values().removeIf(path -> path == null || !Files.exists(path));
        if (resolved.isEmpty()) {
            warnings.add("No texture assets were resolved");
        }
        return resolved;
    }

    private static void putTexture(Map<String, Path> resolved, Path root, String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return;
        }
        Path path = root.resolve(rawPath.replace('\\', '/')).normalize();
        String id = textureId(rawPath);
        resolved.putIfAbsent(id, path);
    }

    private static Path conventionalTexture(Path root, String textureId) {
        if (textureId == null || textureId.isBlank()) {
            return null;
        }
        return root.resolve("textures").resolve(textureId + ".png").normalize();
    }

    private static String textureId(String rawPath) {
        String normalized = rawPath.replace('\\', '/').toLowerCase(Locale.ROOT);
        if (normalized.startsWith("textures/")) {
            normalized = normalized.substring("textures/".length());
        }
        if (normalized.endsWith(".png")) {
            normalized = normalized.substring(0, normalized.length() - 4);
        }
        return normalized;
    }

    private static Path resolveAsset(Path root, String manifestValue, String fallback) {
        String relative = manifestValue == null || manifestValue.isBlank() ? fallback : manifestValue.replace('\\', '/');
        Path candidate = root.resolve(relative);
        return Files.exists(candidate) ? candidate.normalize() : null;
    }

    private static JsonObject readJsonIfExists(Path path) throws IOException {
        if (path == null || !Files.exists(path)) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonElement element = JsonParser.parseReader(reader);
            return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
        }
    }

    private static JsonObject child(JsonObject root, String key) {
        if (root == null || !root.has(key) || !root.get(key).isJsonObject()) {
            return null;
        }
        return root.getAsJsonObject(key);
    }

    private static String getString(JsonObject root, String key) {
        if (root == null || !root.has(key) || !root.get(key).isJsonPrimitive()) {
            return null;
        }
        JsonElement element = root.get(key);
        return element.getAsJsonPrimitive().isString() ? element.getAsString() : null;
    }
}
