package com.elfmcys.yesstevemodel.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.util.Identifier;
import com.elfmcys.yesstevemodel.YesSteveModel;

public final class YsmGeoPackCompiler {
    private static long compileRevision;
    private static final Pattern FIRST_PERSON_HIDDEN_BONE_PATTERN = Pattern.compile(
        "head|face|hair|hat|eye|brow|eyelid|pupil|mouth|ear|bang",
        Pattern.CASE_INSENSITIVE
    );

    public YsmGeoPack compile(YsmCompiledPack compiledPack) throws IOException {
        YsmPackDescriptor descriptor = compiledPack.descriptor();
        YsmSourcePack sourcePack = compiledPack.sourcePack();
        if (sourcePack == null || !sourcePack.hasMainModel()) {
            throw new IOException("Pack is missing a player main model");
        }
        if (!sourcePack.hasMainAnimation()) {
            throw new IOException("Pack is missing a main animation");
        }

        String basePath = descriptor.sourceType() == YsmPackDescriptor.SourceType.BUILTIN
            ? "builtin/" + sanitizeResourcePath(descriptor.legacyModelId())
            : "imported/" + sanitizeResourcePath(descriptor.id());
        String versionedBasePath = basePath + "/r" + nextCompileRevision();
        Identifier modelResource = Identifier.of(YesSteveModel.MOD_ID, versionedBasePath + "/main");
        Identifier animationResource = Identifier.of(YesSteveModel.MOD_ID, versionedBasePath + "/main");
        Identifier textureResource = Identifier.of(
            YesSteveModel.MOD_ID,
            versionedBasePath + "/textures/" + sanitizeResourcePath(sourcePack.selectedTextureId())
        );
        YsmGeoResourceBridge.registerPack(
            modelResource,
            sourcePack.mainModel(),
            animationResource,
            sourcePack.mainAnimation(),
            textureResource,
            resolveTexturePath(sourcePack)
        );
        Set<String> animationNames = readAnimationNames(sourcePack.mainAnimation());
        if (animationNames.isEmpty()) {
            throw new IOException("Pack contains no named animations");
        }
        YsmScaleProfile scaleProfile = computeScaleProfile(sourcePack.mainModel());
        Set<String> firstPersonHiddenBones = computeFirstPersonHiddenBones(sourcePack.mainModel());

        return new YsmGeoPack(compiledPack, modelResource, textureResource, animationResource, animationNames, scaleProfile, firstPersonHiddenBones);
    }

    private static synchronized long nextCompileRevision() {
        return ++compileRevision;
    }

    private static Set<String> readAnimationNames(JsonObject animationRoot) {
        Set<String> names = new LinkedHashSet<>();
        if (animationRoot == null) {
            return names;
        }
        JsonElement animations = animationRoot.get("animations");
        if (animations != null && animations.isJsonObject()) {
            names.addAll(animations.getAsJsonObject().keySet());
        }
        return names;
    }

    private static Path resolveTexturePath(YsmSourcePack sourcePack) throws IOException {
        if (sourcePack.selectedTexturePath() != null) {
            return sourcePack.selectedTexturePath();
        }

        for (Map.Entry<String, Path> entry : sourcePack.textures().entrySet()) {
            if (entry.getValue() != null) {
                return entry.getValue();
            }
        }

        throw new IOException("Pack is missing a texture resource");
    }

    private static String sanitizeResourcePath(String value) {
        if (value == null || value.isBlank()) {
            return "default";
        }
        return value
            .toLowerCase(Locale.ROOT)
            .replace('\\', '/')
            .replaceAll("[^a-z0-9/_-]+", "_")
            .replaceAll("/+", "/")
            .replaceAll("^/+", "")
            .replaceAll("/+$", "");
    }

    private static YsmScaleProfile computeScaleProfile(JsonObject mainModel) {
        if (mainModel == null) {
            return YsmScaleProfile.DEFAULT;
        }

        try {
            JsonArray geometries = mainModel.getAsJsonArray("minecraft:geometry");
            if (geometries == null || geometries.isEmpty() || !geometries.get(0).isJsonObject()) {
                return YsmScaleProfile.DEFAULT;
            }

            JsonObject geometry = geometries.get(0).getAsJsonObject();
            JsonArray bones = geometry.getAsJsonArray("bones");
            if (bones == null || bones.isEmpty()) {
                return YsmScaleProfile.DEFAULT;
            }

            float minY = Float.POSITIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;
            for (JsonElement boneElement : bones) {
                if (!boneElement.isJsonObject()) {
                    continue;
                }
                JsonArray cubes = boneElement.getAsJsonObject().getAsJsonArray("cubes");
                if (cubes == null) {
                    continue;
                }
                for (JsonElement cubeElement : cubes) {
                    if (!cubeElement.isJsonObject()) {
                        continue;
                    }
                    JsonObject cube = cubeElement.getAsJsonObject();
                    JsonArray origin = cube.getAsJsonArray("origin");
                    JsonArray size = cube.getAsJsonArray("size");
                    if (origin == null || size == null || origin.size() < 2 || size.size() < 2) {
                        continue;
                    }
                    float cubeMinY = origin.get(1).getAsFloat();
                    float cubeMaxY = cubeMinY + size.get(1).getAsFloat();
                    minY = Math.min(minY, cubeMinY);
                    maxY = Math.max(maxY, cubeMaxY);
                }
            }

            if (!Float.isFinite(minY) || !Float.isFinite(maxY) || maxY <= minY) {
                return YsmScaleProfile.DEFAULT;
            }

            float modelHeight = maxY - minY;
            float scale = 32.0f / modelHeight;
            float worldTranslateY = (-minY * scale) / 16.0f + 0.01f;
            return new YsmScaleProfile(scale, worldTranslateY, modelHeight, minY);
        } catch (Throwable throwable) {
            return YsmScaleProfile.DEFAULT;
        }
    }

    private static Set<String> computeFirstPersonHiddenBones(JsonObject mainModel) {
        Set<String> hidden = new LinkedHashSet<>();
        if (mainModel == null) {
            return hidden;
        }

        try {
            JsonArray geometries = mainModel.getAsJsonArray("minecraft:geometry");
            if (geometries == null || geometries.isEmpty() || !geometries.get(0).isJsonObject()) {
                return hidden;
            }

            JsonObject geometry = geometries.get(0).getAsJsonObject();
            JsonArray bones = geometry.getAsJsonArray("bones");
            if (bones == null || bones.isEmpty()) {
                return hidden;
            }

            Map<String, List<String>> children = new HashMap<>();
            Set<String> seeds = new LinkedHashSet<>();
            for (JsonElement boneElement : bones) {
                if (!boneElement.isJsonObject()) {
                    continue;
                }
                JsonObject bone = boneElement.getAsJsonObject();
                String name = getString(bone, "name");
                if (name == null || name.isBlank()) {
                    continue;
                }
                if (FIRST_PERSON_HIDDEN_BONE_PATTERN.matcher(name).find()) {
                    seeds.add(name);
                }
                String parent = getString(bone, "parent");
                if (parent != null && !parent.isBlank()) {
                    children.computeIfAbsent(parent, ignored -> new ArrayList<>()).add(name);
                }
            }

            ArrayDeque<String> queue = new ArrayDeque<>(seeds);
            while (!queue.isEmpty()) {
                String current = queue.removeFirst();
                if (!hidden.add(current)) {
                    continue;
                }
                for (String child : children.getOrDefault(current, List.of())) {
                    queue.addLast(child);
                }
            }
        } catch (Throwable throwable) {
            return hidden;
        }

        return hidden;
    }

    private static String getString(JsonObject root, String key) {
        if (root == null || !root.has(key) || !root.get(key).isJsonPrimitive()) {
            return null;
        }
        JsonElement element = root.get(key);
        return element.getAsJsonPrimitive().isString() ? element.getAsString() : null;
    }

}
