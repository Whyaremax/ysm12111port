package com.elfmcys.yesstevemodel.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import net.minecraft.util.Identifier;
import com.elfmcys.yesstevemodel.YesSteveModel;

public final class YsmGeoPackCompiler {
    private static long compileRevision;

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

        return new YsmGeoPack(compiledPack, modelResource, textureResource, animationResource, animationNames, scaleProfile);
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
}
