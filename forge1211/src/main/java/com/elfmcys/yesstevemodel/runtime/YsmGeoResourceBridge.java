package com.elfmcys.yesstevemodel.runtime;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.platform.NativeImage;
import com.elfmcys.yesstevemodel.YesSteveModel;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.cache.BakedAnimationCache;
import software.bernie.geckolib.cache.BakedModelCache;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.loading.json.raw.Model;

public final class YsmGeoResourceBridge {
    private static final Field MODELS_FIELD = findField("MODELS");
    private static final Field ANIMATIONS_FIELD = findField("ANIMATIONS");

    private YsmGeoResourceBridge() {
    }

    public static synchronized void registerPack(
        Identifier modelId,
        JsonObject modelJson,
        Identifier animationId,
        JsonObject animationRoot,
        Identifier textureId,
        Path texturePath
    ) throws IOException {
        installModel(modelId, modelJson);
        installAnimation(animationId, animationRoot);
        installTexture(textureId, texturePath);
    }

    public static synchronized java.util.Optional<BakedGeoModel> getInstalledModel(Identifier modelId) throws IOException {
        try {
            BakedModelCache currentCache = (BakedModelCache) MODELS_FIELD.get(null);
            return java.util.Optional.ofNullable(currentCache.cache().get(modelId));
        } catch (ReflectiveOperationException exception) {
            throw new IOException("Failed to inspect GeckoLib model cache", exception);
        }
    }

    private static void installModel(Identifier modelId, JsonObject modelJson) throws IOException {
        try {
            BakedModelCache currentCache = (BakedModelCache) MODELS_FIELD.get(null);
            Map<Identifier, BakedGeoModel> merged = new LinkedHashMap<>(currentCache.cache());
            Model model = GeckoLibResources.GSON.fromJson(modelJson, Model.class);
            BakedGeoModel bakedModel = BakedModelFactory.getForNamespace(modelId.getNamespace())
                .constructGeoModel(GeometryTree.fromModel(model));
            merged.put(modelId, bakedModel);
            MODELS_FIELD.set(null, new BakedModelCache(Map.copyOf(merged)));
        } catch (ReflectiveOperationException exception) {
            throw new IOException("Failed to install imported GeckoLib model cache", exception);
        }
    }

    private static void installAnimation(Identifier animationId, JsonObject animationRoot) throws IOException {
        try {
            JsonObject sanitizedRoot = sanitizeAnimationRoot(animationRoot);
            JsonObject animations = sanitizedRoot.getAsJsonObject("animations");
            if (animations == null) {
                throw new IOException("Animation file is missing the animations object");
            }

            BakedAnimationCache currentCache = (BakedAnimationCache) ANIMATIONS_FIELD.get(null);
            Map<Identifier, BakedAnimations> merged = new LinkedHashMap<>(currentCache.cache());
            merged.put(animationId, GeckoLibResources.GSON.fromJson(animations, BakedAnimations.class));
            ANIMATIONS_FIELD.set(null, new BakedAnimationCache(Map.copyOf(merged)));
        } catch (ReflectiveOperationException exception) {
            throw new IOException("Failed to install imported GeckoLib animation cache", exception);
        }
    }

    private static void installTexture(Identifier textureId, Path texturePath) throws IOException {
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            throw new IOException("Minecraft client is unavailable");
        }
        if (client.getTextureManager() == null) {
            throw new IOException("Texture manager is not ready yet");
        }

        try (InputStream inputStream = Files.newInputStream(texturePath)) {
            NativeImage image = NativeImage.read(inputStream);
            client.getTextureManager().release(textureId);
            DynamicTexture texture = new DynamicTexture(() -> "YSM " + textureId, image);
            texture.upload();
            client.getTextureManager().register(textureId, texture);
        }
    }

    private static Field findField(String name) {
        try {
            Field field = GeckoLibResources.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException exception) {
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("Failed to bind GeckoLib resource cache field {}", name, exception);
            throw new RuntimeException(exception);
        }
    }

    private static JsonObject sanitizeAnimationRoot(JsonObject animationRoot) {
        JsonObject copy = animationRoot.deepCopy();
        return sanitizeObject(copy);
    }

    private static JsonObject sanitizeObject(JsonObject object) {
        JsonObject sanitized = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            sanitized.add(entry.getKey(), sanitizeElement(entry.getValue()));
        }
        return sanitized;
    }

    private static JsonElement sanitizeElement(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return element;
        }
        if (element.isJsonObject()) {
            return sanitizeObject(element.getAsJsonObject());
        }
        if (element.isJsonArray()) {
            JsonArray array = new JsonArray();
            for (JsonElement child : element.getAsJsonArray()) {
                array.add(sanitizeElement(child));
            }
            return array;
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String expression = element.getAsString();
            if (looksLikeExpression(expression)) {
                return new JsonPrimitive(sanitizeExpression(expression));
            }
        }
        return element;
    }

    private static boolean looksLikeExpression(String expression) {
        return expression.contains("ysm.")
            || expression.contains("query.")
            || expression.contains("q.")
            || expression.contains("math.")
            || expression.contains("v.")
            || expression.contains("ctrl.")
            || expression.contains("?")
            || expression.contains(";");
    }

    private static String sanitizeExpression(String expression) {
        String sanitized = expression.trim();
        if (sanitized.isEmpty()) {
            return sanitized;
        }

        sanitized = sanitized.replace("q.", "query.");
        sanitized = sanitized.replaceAll("\\bctrl\\.[A-Za-z0-9_]+\\b", "0");
        sanitized = sanitized.replaceAll("[A-Za-z0-9_:.]+\\s*!=\\s*'[^']*'", "1");
        sanitized = sanitized.replaceAll("[A-Za-z0-9_:.]+\\s*==\\s*'[^']*'", "0");
        sanitized = stripSecondOrderCalls(sanitized);
        sanitized = stripBoneRotCalls(sanitized);
        sanitized = stripNullCoalescing(sanitized);

        if (containsUnsupportedAssignment(sanitized) || sanitized.contains(";")) {
            return "0";
        }

        sanitized = rewriteParenthesizedTernaries(sanitized);
        sanitized = rewriteWholeTernary(sanitized);
        return sanitized;
    }

    private static boolean containsUnsupportedAssignment(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            char current = expression.charAt(i);
            if (current != '=') {
                continue;
            }
            char previous = i > 0 ? expression.charAt(i - 1) : '\0';
            char next = i + 1 < expression.length() ? expression.charAt(i + 1) : '\0';
            if (previous != '<' && previous != '>' && previous != '!' && previous != '=' && next != '=') {
                return true;
            }
        }
        return false;
    }

    private static String stripNullCoalescing(String expression) {
        String sanitized = expression;
        int marker = sanitized.indexOf("??");
        while (marker >= 0) {
            int leftStart = marker - 1;
            while (leftStart >= 0 && " ()+-*/<>=!&|,".indexOf(sanitized.charAt(leftStart)) < 0) {
                leftStart--;
            }
            int rightEnd = marker + 2;
            while (rightEnd < sanitized.length() && " ()+-*/<>=!&|,".indexOf(sanitized.charAt(rightEnd)) < 0) {
                rightEnd++;
            }
            String left = sanitized.substring(leftStart + 1, marker).trim();
            sanitized = sanitized.substring(0, leftStart + 1) + left + sanitized.substring(rightEnd);
            marker = sanitized.indexOf("??");
        }
        return sanitized;
    }

    private static String stripSecondOrderCalls(String expression) {
        String sanitized = expression;
        String function = "ysm.second_order(";
        int start = sanitized.indexOf(function);
        while (start >= 0) {
            int open = start + function.length() - 1;
            int close = findMatchingParenthesis(sanitized, open);
            if (close < 0) {
                return "0";
            }
            String argumentText = sanitized.substring(open + 1, close);
            String replacement = "0";
            java.util.List<String> arguments = splitTopLevelArguments(argumentText);
            if (arguments.size() >= 2) {
                replacement = "(" + sanitizeExpression(arguments.get(1)) + ")";
            }
            sanitized = sanitized.substring(0, start) + replacement + sanitized.substring(close + 1);
            start = sanitized.indexOf(function);
        }
        return sanitized;
    }

    private static String stripBoneRotCalls(String expression) {
        String sanitized = expression;
        sanitized = sanitized.replaceAll("ysm\\.bone_rot\\([^)]*\\)\\.[xyz]", "0");
        sanitized = sanitized.replaceAll("ysm\\.bone_rot\\([^)]*\\)", "0");

        return sanitized;
    }

    private static String rewriteParenthesizedTernaries(String expression) {
        String sanitized = expression;
        boolean changed = true;

        while (changed) {
            changed = false;
            java.util.ArrayDeque<Integer> stack = new java.util.ArrayDeque<>();
            boolean singleQuoted = false;

            for (int i = 0; i < sanitized.length(); i++) {
                char current = sanitized.charAt(i);

                if (current == '\'') {
                    singleQuoted = !singleQuoted;
                    continue;
                }

                if (singleQuoted) {
                    continue;
                }

                if (current == '(') {
                    stack.push(i);
                    continue;
                }

                if (current != ')' || stack.isEmpty()) {
                    continue;
                }

                int open = stack.pop();
                String inner = sanitized.substring(open + 1, i);

                if (!hasTopLevelTernary(inner)) {
                    continue;
                }

                sanitized =
                    sanitized.substring(0, open + 1)
                        + rewriteWholeTernary(inner)
                        + sanitized.substring(i);

                changed = true;
                break;
            }
        }

        return sanitized;
    }

    private static boolean hasTopLevelTernary(String expression) {
        return findTopLevelQuestionMark(expression) >= 0;
    }

    private static String rewriteWholeTernary(String expression) {
        int questionMark = findTopLevelQuestionMark(expression);
        if (questionMark < 0) {
            return expression;
        }
        int colon = findMatchingColon(expression, questionMark);
        String condition = sanitizeExpression(expression.substring(0, questionMark).trim());
        String ifTrue = sanitizeExpression(expression.substring(questionMark + 1, colon >= 0 ? colon : expression.length()).trim());
        String ifFalse = colon >= 0 ? sanitizeExpression(expression.substring(colon + 1).trim()) : "0";
        return "((" + condition + ")*(" + ifTrue + ")+(((" + condition + ")<=0)*(" + ifFalse + ")))";
    }

    private static int findTopLevelQuestionMark(String expression) {
        int depth = 0;
        boolean singleQuoted = false;
        for (int i = 0; i < expression.length(); i++) {
            char current = expression.charAt(i);
            if (current == '\'') {
                singleQuoted = !singleQuoted;
                continue;
            }
            if (singleQuoted) {
                continue;
            }
            if (current == '(') {
                depth++;
            } else if (current == ')') {
                depth = Math.max(0, depth - 1);
            } else if (current == '?' && depth == 0) {
                return i;
            }
        }
        return -1;
    }

    private static int findMatchingColon(String expression, int questionMark) {
        int depth = 0;
        int ternaryDepth = 0;
        boolean singleQuoted = false;
        for (int i = questionMark + 1; i < expression.length(); i++) {
            char current = expression.charAt(i);
            if (current == '\'') {
                singleQuoted = !singleQuoted;
                continue;
            }
            if (singleQuoted) {
                continue;
            }
            if (current == '(') {
                depth++;
            } else if (current == ')') {
                depth = Math.max(0, depth - 1);
            } else if (current == '?' && depth == 0) {
                ternaryDepth++;
            } else if (current == ':' && depth == 0) {
                if (ternaryDepth == 0) {
                    return i;
                }
                ternaryDepth--;
            }
        }
        return -1;
    }

    private static int findMatchingParenthesis(String expression, int openIndex) {
        int depth = 0;
        boolean singleQuoted = false;
        for (int i = openIndex; i < expression.length(); i++) {
            char current = expression.charAt(i);
            if (current == '\'') {
                singleQuoted = !singleQuoted;
                continue;
            }
            if (singleQuoted) {
                continue;
            }
            if (current == '(') {
                depth++;
            } else if (current == ')') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static java.util.List<String> splitTopLevelArguments(String expression) {
        java.util.List<String> arguments = new java.util.ArrayList<>();
        int depth = 0;
        boolean singleQuoted = false;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char character = expression.charAt(i);
            if (character == '\'') {
                singleQuoted = !singleQuoted;
            }
            if (!singleQuoted) {
                if (character == '(') {
                    depth++;
                } else if (character == ')') {
                    depth--;
                } else if (character == ',' && depth == 0) {
                    arguments.add(current.toString().trim());
                    current.setLength(0);
                    continue;
                }
            }
            current.append(character);
        }
        arguments.add(current.toString().trim());
        return arguments;
    }
}
