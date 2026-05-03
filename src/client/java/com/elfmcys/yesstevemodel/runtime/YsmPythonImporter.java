package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public final class YsmPythonImporter {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Pattern FORMAT_PATTERN = Pattern.compile("<format>\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern NAME_PATTERN = Pattern.compile("<name>\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TEXTURE_PATTERN = Pattern.compile("<texture>\\s+(\\S+)", Pattern.CASE_INSENSITIVE);
    private static final Set<String> SUPPORTED_FORMATS = Set.of("9", "15", "31");

    private YsmPythonImporter() {
    }

    public static SyncResult syncDroppedPacks(Path cacheRoot) throws IOException, InterruptedException {
        Files.createDirectories(cacheRoot);

        List<String> imported = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        try (var stream = Files.list(cacheRoot)) {
            List<Path> sourceFiles = stream
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".ysm"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase(Locale.ROOT)))
                .toList();

            for (Path sourceFile : sourceFiles) {
                if (!needsImport(sourceFile, cacheRoot)) {
                    skipped.add(sourceFile.getFileName().toString());
                    continue;
                }

                try {
                    YsmPackDescriptor descriptor = importPack(sourceFile, cacheRoot);
                    imported.add(descriptor.displayName());
                } catch (IOException exception) {
                    failed.add(sourceFile.getFileName() + ": " + exception.getMessage());
                }
            }
        }

        return new SyncResult(imported, skipped, failed);
    }

    public static YsmPackDescriptor importPack(Path sourceFile, Path cacheRoot) throws IOException, InterruptedException {
        Files.createDirectories(cacheRoot);
        Path scriptRoot = materializeScripts();
        ProcessResult result = runExtractor(scriptRoot, sourceFile);
        Path dumpFolder = resolveDumpFolder(sourceFile.getParent(), result.outputLines);
        if (dumpFolder == null || !Files.exists(dumpFolder)) {
            throw new IOException("Extractor did not produce a dump folder.\n" + String.join("\n", result.outputLines));
        }

        String baseName = sanitize(sourceFile.getFileName().toString().replaceFirst("\\.ysm$", ""));
        Path target = cacheRoot.resolve("imported_" + baseName);
        deleteRecursively(target);
        Files.createDirectories(target.getParent());
        Files.move(dumpFolder, target, StandardCopyOption.REPLACE_EXISTING);

        ImportedMetadata importedMetadata = inspectImportedFolder(baseName, target, sourceFile);
        YsmPackDescriptor descriptor = new YsmPackDescriptor(
            "imported:" + baseName,
            baseName,
            importedMetadata.displayName(),
            importedMetadata.defaultTextureId(),
            YsmPackDescriptor.SourceType.IMPORTED,
            target,
            importedMetadata.formatLabel(),
            importedMetadata.details(),
            importedMetadata.textureIds(),
            importedMetadata.hasMainModel(),
            importedMetadata.hasArmModel(),
            importedMetadata.hasFpArmAnimation()
        );

        JsonObject json = new JsonObject();
        json.addProperty("id", descriptor.id());
        json.addProperty("legacyModelId", descriptor.legacyModelId());
        json.addProperty("displayName", descriptor.displayName());
        json.addProperty("defaultTextureId", descriptor.defaultTextureId());
        json.addProperty("formatLabel", descriptor.formatLabel());
        json.addProperty("details", "Imported from " + sourceFile.getFileName());
        json.addProperty("sourceFileName", sourceFile.getFileName().toString());
        json.addProperty("sourceFilePath", sourceFile.toAbsolutePath().toString());
        json.addProperty("sourceLastModified", Files.getLastModifiedTime(sourceFile).toMillis());
        try (var writer = Files.newBufferedWriter(target.resolve("descriptor.json"))) {
            GSON.toJson(json, writer);
        }
        return new YsmPackDescriptor(
            descriptor.id(),
            descriptor.legacyModelId(),
            descriptor.displayName(),
            descriptor.defaultTextureId(),
            descriptor.sourceType(),
            target,
            descriptor.formatLabel(),
            "Imported from " + sourceFile.getFileName(),
            descriptor.textureIds(),
            descriptor.hasMainModel(),
            descriptor.hasArmModel(),
            descriptor.hasFpArmAnimation()
        );
    }

    private static boolean needsImport(Path sourceFile, Path cacheRoot) throws IOException {
        String baseName = sanitize(sourceFile.getFileName().toString().replaceFirst("\\.ysm$", ""));
        Path descriptorPath = cacheRoot.resolve("imported_" + baseName).resolve("descriptor.json");
        if (!Files.exists(descriptorPath)) {
            return true;
        }

        try (var reader = Files.newBufferedReader(descriptorPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            String storedName = getString(root, "sourceFileName", "");
            long storedLastModified = root.has("sourceLastModified") ? root.get("sourceLastModified").getAsLong() : Long.MIN_VALUE;
            FileTime currentLastModified = Files.getLastModifiedTime(sourceFile);
            return !sourceFile.getFileName().toString().equals(storedName) || storedLastModified != currentLastModified.toMillis();
        } catch (Throwable throwable) {
            return true;
        }
    }

    private static ProcessResult runExtractor(Path scriptRoot, Path sourceFile) throws IOException, InterruptedException {
        List<List<String>> commands = new ArrayList<>();
        if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            commands.addAll(findWindowsPythonCommands(sourceFile));
            commands.add(List.of("py", "-3", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
        }
        commands.add(List.of("python3", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
        commands.add(List.of("python", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));

        IOException lastIo = null;
        for (List<String> command : commands) {
            try {
                Process process = new ProcessBuilder(command)
                    .directory(scriptRoot.toFile())
                    .redirectErrorStream(true)
                    .start();
                List<String> outputLines = new ArrayList<>();
                try (InputStream in = process.getInputStream()) {
                    new String(in.readAllBytes()).lines().forEach(outputLines::add);
                }
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    return new ProcessResult(outputLines);
                }
                throw new IOException("Extractor failed with exit code " + exitCode + "\n" + String.join("\n", outputLines));
            } catch (IOException exception) {
                lastIo = exception;
            }
        }

        throw lastIo == null ? new IOException("No Python interpreter was found for YSM import.") : lastIo;
    }

    private static List<List<String>> findWindowsPythonCommands(Path sourceFile) {
        List<List<String>> commands = new ArrayList<>();
        Set<Path> candidates = new LinkedHashSet<>();

        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isBlank()) {
            candidates.addAll(findWindowsPythonExecutables(Path.of(localAppData).resolve("Programs").resolve("Python")));
        }

        String userHome = System.getProperty("user.home", "");
        if (!userHome.isBlank()) {
            candidates.addAll(findWindowsPythonExecutables(
                Path.of(userHome).resolve("AppData").resolve("Local").resolve("Programs").resolve("Python")
            ));
            candidates.add(Path.of(userHome).resolve("AppData").resolve("Local").resolve("Programs").resolve("Python").resolve("Launcher").resolve("py.exe"));
        }

        String userProfile = System.getenv("USERPROFILE");
        if (userProfile != null && !userProfile.isBlank()) {
            candidates.addAll(findWindowsPythonExecutables(
                Path.of(userProfile).resolve("AppData").resolve("Local").resolve("Programs").resolve("Python")
            ));
        }

        String programFiles = System.getenv("ProgramFiles");
        if (programFiles != null && !programFiles.isBlank()) {
            candidates.addAll(findWindowsPythonExecutables(Path.of(programFiles).resolve("Python")));
        }

        String programFilesX86 = System.getenv("ProgramFiles(x86)");
        if (programFilesX86 != null && !programFilesX86.isBlank()) {
            candidates.addAll(findWindowsPythonExecutables(Path.of(programFilesX86).resolve("Python")));
        }

        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                String name = candidate.getFileName().toString().toLowerCase(Locale.ROOT);
                if ("py.exe".equals(name)) {
                    commands.add(List.of(candidate.toString(), "-3", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
                } else {
                    commands.add(List.of(candidate.toString(), "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
                }
            }
        }

        commands.addAll(findWherePythonCommands(sourceFile));

        return commands;
    }

    private static List<Path> findWindowsPythonExecutables(Path pythonRoot) {
        if (!Files.isDirectory(pythonRoot)) {
            return List.of();
        }

        try (var stream = Files.list(pythonRoot)) {
            return stream
                .filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).startsWith("python"))
                .map(path -> path.resolve("python.exe"))
                .filter(Files::isRegularFile)
                .sorted(Comparator.comparing((Path path) -> path.getParent().getFileName().toString(), String.CASE_INSENSITIVE_ORDER).reversed())
                .toList();
        } catch (IOException exception) {
            return List.of();
        }
    }

    private static List<List<String>> findWherePythonCommands(Path sourceFile) {
        List<List<String>> commands = new ArrayList<>();
        for (String binary : List.of("python.exe", "py.exe", "python", "py")) {
            commands.addAll(findWhereCommand(binary, sourceFile));
        }
        return commands;
    }

    private static List<List<String>> findWhereCommand(String binary, Path sourceFile) {
        try {
            Process process = new ProcessBuilder("where.exe", binary)
                .redirectErrorStream(true)
                .start();
            List<String> lines;
            try (InputStream in = process.getInputStream()) {
                lines = new String(in.readAllBytes()).lines().toList();
            }
            if (process.waitFor() != 0) {
                return List.of();
            }

            List<List<String>> commands = new ArrayList<>();
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                Path candidate = Path.of(line.trim());
                if (!Files.isRegularFile(candidate)) {
                    continue;
                }
                String name = candidate.getFileName().toString().toLowerCase(Locale.ROOT);
                if ("py.exe".equals(name)) {
                    commands.add(List.of(candidate.toString(), "-3", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
                } else {
                    commands.add(List.of(candidate.toString(), "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
                }
            }
            return commands;
        } catch (IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            return List.of();
        }
    }

    private static ImportedMetadata inspectImportedFolder(String baseName, Path target, Path sourceFile) throws IOException {
        Path propertyFile = target.resolve("property.txt");
        if (Files.exists(propertyFile)) {
            String propertyText = Files.readString(propertyFile);
            Optional<String> format = firstMatch(FORMAT_PATTERN, propertyText);
            if (format.isPresent()) {
                validateFormat(format.get());
            }

            String displayName = firstMatch(NAME_PATTERN, propertyText).orElse(baseName);
            String texture = sanitizeTexture(firstMatch(TEXTURE_PATTERN, propertyText).orElse("default"));
            String formatLabel = format.map(value -> "format " + value).orElse("imported");
            return new ImportedMetadata(
                displayName,
                texture,
                formatLabel,
                "Imported from " + sourceFile.getFileName(),
                scanTextureIds(target, texture),
                Files.exists(target.resolve("models/main.json")),
                Files.exists(target.resolve("models/arm.json")),
                Files.exists(target.resolve("animations/fp.arm.animation.json")) || Files.exists(target.resolve("animations/arm.animation.json"))
            );
        }

        Path ysmJson = target.resolve("ysm.json");
        if (Files.exists(ysmJson)) {
            try (var reader = Files.newBufferedReader(ysmJson)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject metadata = root.has("metadata") ? root.getAsJsonObject("metadata") : new JsonObject();
                JsonObject properties = root.has("properties") ? root.getAsJsonObject("properties") : new JsonObject();
                return new ImportedMetadata(
                    getString(metadata.get("name"), baseName),
                    sanitizeTexture(getString(properties.get("default_texture"), "default")),
                    "imported",
                    "Imported from " + sourceFile.getFileName(),
                    scanTextureIds(target, sanitizeTexture(getString(properties.get("default_texture"), "default"))),
                    Files.exists(target.resolve("models/main.json")),
                    Files.exists(target.resolve("models/arm.json")),
                    Files.exists(target.resolve("animations/fp.arm.animation.json")) || Files.exists(target.resolve("animations/arm.animation.json"))
                );
            }
        }

        Set<String> presentAssets = new LinkedHashSet<>();
        try (var stream = Files.list(target)) {
            stream.map(path -> path.getFileName().toString()).sorted().forEach(presentAssets::add);
        }
        return new ImportedMetadata(
            baseName,
            "default",
            "imported",
            "Imported from " + sourceFile.getFileName() + " (" + String.join(", ", presentAssets) + ")",
            scanTextureIds(target, "default"),
            Files.exists(target.resolve("models/main.json")),
            Files.exists(target.resolve("models/arm.json")),
            Files.exists(target.resolve("animations/fp.arm.animation.json")) || Files.exists(target.resolve("animations/arm.animation.json"))
        );
    }

    private static void validateFormat(String format) throws IOException {
        if ("1".equals(format)) {
            throw new IOException("YSM format 1 is not supported by this 1.21.11 port.");
        }
        if (!SUPPORTED_FORMATS.contains(format)) {
            throw new IOException("Unsupported YSM format " + format + ". Supported formats: 9, 15, 31.");
        }
    }

    private static Path materializeScripts() throws IOException {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(YesSteveModel.MOD_ID);
        if (modContainer.isEmpty()) {
            throw new IOException("Missing YSM mod container");
        }
        Optional<Path> resourceRoot = modContainer.get().findPath("python/ysm");
        if (resourceRoot.isEmpty()) {
            throw new IOException("Bundled extractor scripts are missing");
        }

        Path tempRoot = FabricLoader.getInstance().getGameDir().resolve(".ysm-python");
        Files.createDirectories(tempRoot);
        try (var walk = Files.walk(resourceRoot.get())) {
            walk.forEach(source -> {
                try {
                    Path relative = resourceRoot.get().relativize(source);
                    Path target = tempRoot.resolve(relative.toString());
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(target);
                    } else {
                        Files.createDirectories(target.getParent());
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            });
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
        return tempRoot;
    }

    private static Path resolveDumpFolder(Path baseDir, List<String> lines) {
        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = lines.get(i).trim();
            if (!line.startsWith("dump_folder:")) {
                continue;
            }
            String value = line.substring("dump_folder:".length()).trim();
            if (value.isEmpty()) {
                continue;
            }
            Path path = Path.of(value);
            return path.isAbsolute() ? path : baseDir.resolve(path).normalize();
        }
        return null;
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

    private static List<String> scanTextureIds(Path packRoot, String defaultTextureId) throws IOException {
        Set<String> textureIds = new LinkedHashSet<>();
        Path textureRoot = packRoot.resolve("textures");
        if (Files.exists(textureRoot)) {
            try (var walk = Files.walk(textureRoot)) {
                walk.filter(Files::isRegularFile).forEach(path -> {
                    String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
                    if (!fileName.endsWith(".png")) {
                        return;
                    }
                    Path relative = textureRoot.relativize(path);
                    String normalized = relative.toString().replace('\\', '/');
                    textureIds.add(sanitizeTexture(normalized));
                });
            }
        }
        textureIds.add(sanitizeTexture(defaultTextureId));
        return List.copyOf(textureIds);
    }

    private static String getString(JsonObject root, String key, String fallback) {
        return getString(root.get(key), fallback);
    }

    private static String getString(com.google.gson.JsonElement element, String fallback) {
        return element != null && !element.isJsonNull() ? element.getAsString() : fallback;
    }

    private static String sanitize(String name) {
        String sanitized = name.replaceAll("[^A-Za-z0-9._-]+", "_");
        return sanitized.isBlank() ? "imported" : sanitized;
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var walk = Files.walk(path)) {
            walk.sorted((left, right) -> right.getNameCount() - left.getNameCount()).forEach(entry -> {
                try {
                    Files.deleteIfExists(entry);
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            });
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
    }

    private record ProcessResult(List<String> outputLines) {
    }

    private record ImportedMetadata(
        String displayName,
        String defaultTextureId,
        String formatLabel,
        String details,
        List<String> textureIds,
        boolean hasMainModel,
        boolean hasArmModel,
        boolean hasFpArmAnimation
    ) {
    }

    public record SyncResult(
        List<String> imported,
        List<String> skipped,
        List<String> failed
    ) {
    }
}
