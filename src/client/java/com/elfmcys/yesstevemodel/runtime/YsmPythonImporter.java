package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.threedmodelnow.core.ThreeDModelNow;
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
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public final class YsmPythonImporter {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Pattern FORMAT_PATTERN = Pattern.compile("<format>\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern NAME_PATTERN = Pattern.compile("<name>\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TEXTURE_PATTERN = Pattern.compile("<texture>\\s+(\\S+)", Pattern.CASE_INSENSITIVE);
    private static final String YSM_PARSER_EXECUTABLE_PROPERTY = "yes_steve_model.ysmparser.executable";
    private static final String YSM_PARSER_BUNDLE_PROPERTY = "yes_steve_model.ysmparser.bundle";
    private static final String YSM_PARSER_EXECUTABLE_ENV = "YSM_PARSER_EXECUTABLE";
    private static final String YSM_PARSER_BUNDLE_ENV = "YSM_PARSER_BUNDLE";
    private static final Set<String> SUPPORTED_FORMATS = Set.of("9", "15", "31");
    private static final Set<String> RAW_MODEL_FILES = Set.of("main.json", "arm.json");
    private static final Set<String> RAW_ANIMATION_FILES = Set.of("main.animation.json", "arm.animation.json", "fp.arm.animation.json", "fp_arm.animation.json");

    private YsmPythonImporter() {
    }

    public static SyncResult syncDroppedPacks(Path cacheRoot) throws IOException, InterruptedException {
        Files.createDirectories(cacheRoot);

        List<String> imported = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        try (var stream = Files.list(cacheRoot)) {
            List<Path> sourceFiles = stream
                .filter(YsmPythonImporter::isSupportedImportSource)
                .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase(Locale.ROOT)))
                .toList();

            for (Path sourceFile : sourceFiles) {
                if (!needsImport(sourceFile, cacheRoot.resolve(targetDirectoryName(sourceFile)))) {
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
        String baseName = sourceBaseName(sourceFile);
        Path target = cacheRoot.resolve(targetDirectoryName(sourceFile));
        deleteRecursively(target);
        Files.createDirectories(target.getParent());

        String lowerName = sourceFile.getFileName().toString().toLowerCase(Locale.ROOT);
        String importBackend;
        if (Files.isDirectory(sourceFile)) {
            copyRecursively(sourceFile, target);
            importBackend = "loose folder copy";
        } else if (lowerName.endsWith(".zip")) {
            extractZip(sourceFile, target);
            importBackend = "zip archive";
        } else {
            importBackend = importYsmPack(sourceFile, target);
        }

        normalizeImportedLayout(baseName, target);

        ImportedMetadata importedMetadata = inspectImportedFolder(baseName, target, sourceFile, importBackend);
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
        json.addProperty("details", importedMetadata.details());
        json.addProperty("importBackend", importBackend);
        json.addProperty("sourceFileName", sourceFile.getFileName().toString());
        json.addProperty("sourceFilePath", sourceFile.toAbsolutePath().toString());
        json.addProperty("sourceLastModified", sourceLastModified(sourceFile));
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
            importedMetadata.details(),
            descriptor.textureIds(),
            descriptor.hasMainModel(),
            descriptor.hasArmModel(),
            descriptor.hasFpArmAnimation()
        );
    }

    private static String importYsmPack(Path sourceFile, Path target) throws IOException, InterruptedException {
        IOException parserFailure = null;
        Optional<Path> parserExecutable = resolveYsmParserExecutable();
        if (parserExecutable.isPresent()) {
            try {
                runYsmParser(parserExecutable.get(), sourceFile, target);
                return "OpenYSM/YSMParser";
            } catch (IOException exception) {
                parserFailure = exception;
                YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.warn(
                    "YSMParser import failed for {}, falling back to Python extractor: {}",
                    sourceFile.getFileName(),
                    exception.getMessage()
                );
            }
        }

        Path scriptRoot = materializeScripts();
        ProcessResult result = runExtractor(scriptRoot, sourceFile);
        Path exported = resolveExportedFolder(sourceFile.getParent(), result.outputLines);
        if (exported == null || !Files.exists(exported)) {
            throw new IOException("Extractor did not produce a dump folder.\n" + String.join("\n", result.outputLines));
        }
        Files.move(exported, target, StandardCopyOption.REPLACE_EXISTING);

        if (parserFailure != null) {
            return "bundled Python extractor fallback";
        }
        if (parserExecutable.isPresent()) {
            return "bundled Python extractor";
        }
        return "bundled Python extractor (YSMParser unavailable)";
    }

    private static boolean needsImport(Path sourceFile, Path target) throws IOException {
        Path descriptorPath = target.resolve("descriptor.json");
        if (!Files.exists(descriptorPath)) {
            return true;
        }

        try (var reader = Files.newBufferedReader(descriptorPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            String storedName = getString(root, "sourceFileName", "");
            long storedLastModified = root.has("sourceLastModified") ? root.get("sourceLastModified").getAsLong() : Long.MIN_VALUE;
            long currentLastModified = sourceLastModified(sourceFile);
            return !sourceFile.getFileName().toString().equals(storedName) || storedLastModified != currentLastModified;
        } catch (Throwable throwable) {
            return true;
        }
    }

    private static ProcessResult runExtractor(Path scriptRoot, Path sourceFile) throws IOException, InterruptedException {
        List<List<String>> commands = new ArrayList<>();
        if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
            commands.addAll(findWindowsPythonCommands(sourceFile));
        } else {
            commands.add(List.of("python3", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
            commands.add(List.of("python", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
        }

        IOException lastIo = null;
        for (List<String> command : commands) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(command)
                    .directory(scriptRoot.toFile())
                    .redirectErrorStream(true);
                prependBundledToolsPath(processBuilder, scriptRoot);
                Process process = processBuilder.start();
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

    private static void prependBundledToolsPath(ProcessBuilder processBuilder, Path scriptRoot) throws IOException {
        Path toolsDir = materializeBundledTools();
        if (toolsDir == null || !Files.isDirectory(toolsDir)) {
            return;
        }

        String pathKey = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win") ? "Path" : "PATH";
        String existing = processBuilder.environment().getOrDefault(pathKey, processBuilder.environment().getOrDefault("PATH", ""));
        String updated = toolsDir.toString() + java.io.File.pathSeparator + existing;
        processBuilder.environment().put(pathKey, updated);
        processBuilder.environment().put("PATH", updated);
        processBuilder.environment().put("YSM_BUNDLED_TOOLS", toolsDir.toString());
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

        String pythonHome = System.getenv("PYTHONHOME");
        if (pythonHome != null && !pythonHome.isBlank()) {
            Path pythonHomePath = Path.of(pythonHome);
            candidates.add(pythonHomePath);
            candidates.add(pythonHomePath.resolve("python.exe"));
        }

        String pythonExecutable = System.getenv("PYTHON");
        if (pythonExecutable != null && !pythonExecutable.isBlank()) {
            candidates.add(Path.of(pythonExecutable));
        }

        boolean foundConcrete = false;
        for (Path candidate : candidates) {
            if (isConcreteWindowsPython(candidate)) {
                foundConcrete = true;
                String name = candidate.getFileName().toString().toLowerCase(Locale.ROOT);
                if ("py.exe".equals(name)) {
                    commands.add(List.of(candidate.toString(), "-3", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
                } else {
                    commands.add(List.of(candidate.toString(), "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
                }
            }
        }

        List<List<String>> whereCommands = findWherePythonCommands(sourceFile);
        if (!whereCommands.isEmpty()) {
            foundConcrete = true;
            commands.addAll(whereCommands);
        }

        if (!foundConcrete) {
            commands.add(List.of("py", "-3", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
            commands.add(List.of("python3", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
            commands.add(List.of("python", "ysm_extract.py", "--dump-folder", "--no-auto-source-oracle", sourceFile.toString()));
        }

        return commands;
    }

    private static List<Path> findWindowsPythonExecutables(Path pythonRoot) {
        if (!Files.isDirectory(pythonRoot)) {
            return List.of();
        }

        try (var walk = Files.walk(pythonRoot, 3)) {
            return walk
                .filter(path -> path.getFileName().toString().equalsIgnoreCase("python.exe") || path.getFileName().toString().equalsIgnoreCase("py.exe"))
                .filter(YsmPythonImporter::isConcreteWindowsPython)
                .sorted(Comparator.comparing(Path::toString, String.CASE_INSENSITIVE_ORDER))
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
        for (String binary : List.of("python", "py")) {
            commands.addAll(findCmdWhereCommand(binary, sourceFile));
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
                if (!isConcreteWindowsPython(candidate)) {
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

    private static List<List<String>> findCmdWhereCommand(String binary, Path sourceFile) {
        try {
            Process process = new ProcessBuilder("cmd.exe", "/c", "where", binary)
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
                if (!isConcreteWindowsPython(candidate)) {
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

    private static boolean isConcreteWindowsPython(Path candidate) {
        if (candidate == null || !Files.isRegularFile(candidate)) {
            return false;
        }
        String normalized = candidate.toAbsolutePath().toString().replace('\\', '/').toLowerCase(Locale.ROOT);
        return !normalized.contains("/windowsapps/");
    }

    private static ImportedMetadata inspectImportedFolder(String baseName, Path target, Path sourceFile, String importBackend) throws IOException {
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
                importDetails(sourceFile, importBackend),
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
                    importDetails(sourceFile, importBackend),
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
            importDetails(sourceFile, importBackend) + " (" + String.join(", ", presentAssets) + ")",
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

    private static boolean isSupportedImportSource(Path path) {
        String lowerName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (Files.isRegularFile(path)) {
            return lowerName.endsWith(".ysm") || lowerName.endsWith(".zip");
        }
        if (!Files.isDirectory(path) || lowerName.startsWith("imported_") || Files.exists(path.resolve("descriptor.json"))) {
            return false;
        }
        return hasLoosePackAssets(path);
    }

    private static boolean hasLoosePackAssets(Path root) {
        try (var walk = Files.walk(root)) {
            return walk
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString().toLowerCase(Locale.ROOT))
                .anyMatch(name ->
                    name.equals("ysm.json")
                        || name.equals("property.txt")
                        || RAW_MODEL_FILES.contains(name)
                        || RAW_ANIMATION_FILES.contains(name)
                        || name.endsWith(".png")
                );
        } catch (IOException exception) {
            return false;
        }
    }

    private static String targetDirectoryName(Path sourceFile) {
        return "imported_" + sourceBaseName(sourceFile);
    }

    private static String sourceBaseName(Path sourceFile) {
        String name = sourceFile.getFileName().toString();
        if (Files.isRegularFile(sourceFile)) {
            name = name.replaceFirst("(?i)\\.(ysm|zip)$", "");
        }
        return sanitize(name);
    }

    private static long sourceLastModified(Path sourceFile) throws IOException {
        if (Files.isRegularFile(sourceFile)) {
            return Files.getLastModifiedTime(sourceFile).toMillis();
        }
        try (var walk = Files.walk(sourceFile)) {
            return walk
                .map(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toMillis();
                    } catch (IOException exception) {
                        return Long.MIN_VALUE;
                    }
                })
                .max(Long::compareTo)
                .orElse(Long.MIN_VALUE);
        }
    }

    private static void copyRecursively(Path source, Path target) throws IOException {
        try (var walk = Files.walk(source)) {
            for (Path entry : walk.sorted(Comparator.comparingInt(Path::getNameCount)).toList()) {
                Path relative = source.relativize(entry);
                Path destination = target.resolve(relative.toString());
                if (Files.isDirectory(entry)) {
                    Files.createDirectories(destination);
                } else {
                    Files.createDirectories(destination.getParent());
                    Files.copy(entry, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void extractZip(Path source, Path target) throws IOException {
        Files.createDirectories(target);
        try (InputStream in = Files.newInputStream(source); ZipInputStream zip = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String entryName = entry.getName().replace('\\', '/');
                if (entryName.isBlank()) {
                    continue;
                }
                Path destination = target.resolve(entryName).normalize();
                if (!destination.startsWith(target)) {
                    throw new IOException("Zip entry escapes import root: " + entryName);
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(destination);
                } else {
                    Files.createDirectories(destination.getParent());
                    Files.copy(zip, destination, StandardCopyOption.REPLACE_EXISTING);
                }
                zip.closeEntry();
            }
        }
    }

    private static void normalizeImportedLayout(String baseName, Path target) throws IOException {
        if (Files.exists(target.resolve("ysm.json")) || Files.exists(target.resolve("property.txt"))) {
            return;
        }

        Path mainModel = findLooseAsset(target, "main.json");
        Path armModel = findLooseAsset(target, "arm.json");
        Path mainAnimation = findLooseAsset(target, "main.animation.json");
        Path armAnimation = findLooseAsset(target, "arm.animation.json");
        Path fpArmAnimation = findLooseAsset(target, "fp.arm.animation.json", "fp_arm.animation.json");
        List<Path> textures = findLooseTextures(target);

        if (mainModel == null && armModel == null && textures.isEmpty()) {
            return;
        }

        Path canonicalMain = canonicalizeAsset(mainModel, target.resolve("models/main.json"));
        Path canonicalArm = canonicalizeAsset(armModel, target.resolve("models/arm.json"));
        Path canonicalMainAnimation = canonicalizeAsset(mainAnimation, target.resolve("animations/main.animation.json"));
        Path canonicalArmAnimation = canonicalizeAsset(armAnimation, target.resolve("animations/arm.animation.json"));
        Path canonicalFpArmAnimation = canonicalizeAsset(fpArmAnimation, target.resolve("animations/fp.arm.animation.json"));

        if (canonicalMainAnimation == null && canonicalMain != null) {
            canonicalMainAnimation = writeFallbackMainAnimation(target.resolve("animations/main.animation.json"));
        }

        String defaultTextureId = "default";
        List<String> textureIds = new ArrayList<>();
        for (Path texture : textures) {
            String id = sanitizeTexture(texture.getFileName().toString());
            Path canonical = target.resolve("textures").resolve(id + ".png");
            canonicalizeAsset(texture, canonical);
            textureIds.add(id);
        }
        if (!textureIds.isEmpty()) {
            defaultTextureId = textureIds.contains("default") ? "default" : textureIds.get(0);
        }

        JsonObject root = new JsonObject();
        JsonObject metadata = new JsonObject();
        metadata.addProperty("name", baseName);
        metadata.addProperty("tips", "Imported raw pack");
        root.add("metadata", metadata);

        JsonObject properties = new JsonObject();
        properties.addProperty("default_texture", defaultTextureId);
        root.add("properties", properties);

        JsonObject player = new JsonObject();
        JsonObject files = new JsonObject();
        JsonObject model = new JsonObject();
        JsonObject animation = new JsonObject();
        if (canonicalMain != null) {
            model.addProperty("main", relativeImportPath(target, canonicalMain));
        }
        if (canonicalArm != null) {
            model.addProperty("arm", relativeImportPath(target, canonicalArm));
        }
        if (canonicalMainAnimation != null) {
            animation.addProperty("main", relativeImportPath(target, canonicalMainAnimation));
        }
        if (canonicalArmAnimation != null) {
            animation.addProperty("arm", relativeImportPath(target, canonicalArmAnimation));
        }
        if (canonicalFpArmAnimation != null) {
            animation.addProperty("fp_arm", relativeImportPath(target, canonicalFpArmAnimation));
        }
        player.add("model", model);
        player.add("animation", animation);
        if (!textureIds.isEmpty()) {
            if (textureIds.size() == 1) {
                player.addProperty("texture", "textures/" + textureIds.get(0) + ".png");
            } else {
                var textureArray = new com.google.gson.JsonArray();
                for (String textureId : textureIds) {
                    textureArray.add("textures/" + textureId + ".png");
                }
                player.add("texture", textureArray);
            }
        }
        files.add("player", player);
        root.add("files", files);

        Files.createDirectories(target);
        try (var writer = Files.newBufferedWriter(target.resolve("ysm.json"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(root, writer);
        }
    }

    private static Path findLooseAsset(Path root, String... fileNames) throws IOException {
        for (String fileName : fileNames) {
            Path modelPath = root.resolve("models").resolve(fileName);
            if (Files.isRegularFile(modelPath)) {
                return modelPath;
            }
            Path animationPath = root.resolve("animations").resolve(fileName);
            if (Files.isRegularFile(animationPath)) {
                return animationPath;
            }
            Path directPath = root.resolve(fileName);
            if (Files.isRegularFile(directPath)) {
                return directPath;
            }
        }

        Set<String> wanted = new LinkedHashSet<>();
        for (String fileName : fileNames) {
            wanted.add(fileName.toLowerCase(Locale.ROOT));
        }
        try (var walk = Files.walk(root)) {
            return walk
                .filter(Files::isRegularFile)
                .filter(path -> wanted.contains(path.getFileName().toString().toLowerCase(Locale.ROOT)))
                .sorted(Comparator.comparingInt(path -> root.relativize(path).getNameCount()))
                .findFirst()
                .orElse(null);
        }
    }

    private static List<Path> findLooseTextures(Path root) throws IOException {
        try (var walk = Files.walk(root)) {
            return walk
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png"))
                .sorted(Comparator.comparingInt(path -> root.relativize(path).getNameCount()))
                .toList();
        }
    }

    private static Path canonicalizeAsset(Path source, Path destination) throws IOException {
        if (source == null || !Files.exists(source)) {
            return null;
        }
        if (source.normalize().equals(destination.normalize())) {
            return destination;
        }
        Files.createDirectories(destination.getParent());
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        return destination;
    }

    private static Path writeFallbackMainAnimation(Path destination) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("format_version", "1.8.0");
        root.addProperty("geckolib_format_version", 2);
        JsonObject animations = new JsonObject();
        JsonObject animation = new JsonObject();
        animation.addProperty("loop", true);
        animation.add("bones", new JsonObject());
        animations.add("animation.main", animation);
        root.add("animations", animations);

        Files.createDirectories(destination.getParent());
        try (var writer = Files.newBufferedWriter(destination, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(root, writer);
        }
        return destination;
    }

    private static String relativeImportPath(Path root, Path path) {
        return root.relativize(path).toString().replace('\\', '/');
    }

    private static Path materializeScripts() throws IOException {
        Optional<ModContainer> modContainer = ysmCompatContainer();
        if (modContainer.isEmpty()) {
            throw new IOException("Missing YSM mod container");
        }

        Path tempRoot = FabricLoader.getInstance().getGameDir().resolve(".ysm-python");
        Files.createDirectories(tempRoot);
        Optional<Path> resourceRoot = modContainer.get().findPath("python/ysm");
        if (resourceRoot.isPresent()) {
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
            return tempRoot.resolve("ysm");
        }

        Optional<Path> bundleZip = modContainer.get().findPath("python/ysm_bundle.zip");
        if (bundleZip.isPresent() && Files.exists(bundleZip.get())) {
            extractZip(bundleZip.get(), tempRoot);
            Path extracted = tempRoot.resolve("ysm");
            if (Files.isDirectory(extracted)) {
                return extracted;
            }
        }

        throw new IOException("Bundled extractor scripts are missing");
    }

    private static Path materializeBundledTools() throws IOException {
        Optional<ModContainer> modContainer = ysmCompatContainer();
        if (modContainer.isEmpty()) {
            return null;
        }

        String platform = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win") ? "windows" : "linux";
        String resourcePath = platform.equals("windows") ? "tools/zstd/windows/zstd.exe" : "tools/zstd/linux/zstd";
        Optional<Path> resource = modContainer.get().findPath(resourcePath);
        if (resource.isEmpty() || !Files.exists(resource.get())) {
            return null;
        }

        Path targetRoot = FabricLoader.getInstance().getGameDir().resolve(".ysm-python").resolve("tools").resolve(platform);
        Files.createDirectories(targetRoot);
        Path target = targetRoot.resolve(platform.equals("windows") ? "zstd.exe" : "zstd");
        Files.copy(resource.get(), target, StandardCopyOption.REPLACE_EXISTING);
        target.toFile().setExecutable(true, false);
        return targetRoot;
    }

    private static Optional<Path> resolveYsmParserExecutable() throws IOException {
        Path configuredExecutable = configuredPath(YSM_PARSER_EXECUTABLE_PROPERTY, YSM_PARSER_EXECUTABLE_ENV);
        if (isUsableYsmParserExecutable(configuredExecutable)) {
            return Optional.of(configuredExecutable.toAbsolutePath().normalize());
        }

        Path configuredBundle = configuredPath(YSM_PARSER_BUNDLE_PROPERTY, YSM_PARSER_BUNDLE_ENV);
        Path bundleExecutable = resolveYsmParserExecutableInBundle(configuredBundle);
        if (isUsableYsmParserExecutable(bundleExecutable)) {
            return Optional.of(bundleExecutable.toAbsolutePath().normalize());
        }

        Path bundledExecutable = materializeBundledYsmParser();
        if (isUsableYsmParserExecutable(bundledExecutable)) {
            return Optional.of(bundledExecutable.toAbsolutePath().normalize());
        }

        Path pathExecutable = findExecutableOnPath(ysmParserExecutableName());
        if (isUsableYsmParserExecutable(pathExecutable)) {
            return Optional.of(pathExecutable.toAbsolutePath().normalize());
        }

        return Optional.empty();
    }

    private static Path configuredPath(String propertyKey, String envKey) {
        String configured = System.getProperty(propertyKey, "");
        if (configured.isBlank()) {
            configured = System.getenv(envKey);
        }
        if (configured == null || configured.isBlank()) {
            return null;
        }
        return Path.of(configured.trim());
    }

    private static Path materializeBundledYsmParser() throws IOException {
        Optional<ModContainer> modContainer = ysmCompatContainer();
        if (modContainer.isEmpty()) {
            return null;
        }

        String platform = ysmParserPlatform();
        if (platform == null) {
            return null;
        }

        String resourceRootPath = "tools/ysmparser/" + platform;
        Optional<Path> resourceRoot = modContainer.get().findPath(resourceRootPath);
        if (resourceRoot.isEmpty() || !Files.exists(resourceRoot.get())) {
            return null;
        }

        Path targetRoot = FabricLoader.getInstance().getGameDir().resolve(".ysm-parser").resolve(platform);
        deleteRecursively(targetRoot);
        copyRecursively(resourceRoot.get(), targetRoot);

        Path executable = resolveYsmParserExecutableInBundle(targetRoot);
        if (executable != null && Files.exists(executable)) {
            executable.toFile().setExecutable(true, false);
        }
        return executable;
    }

    private static Optional<ModContainer> ysmCompatContainer() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(ThreeDModelNow.YSM_COMPAT_MOD_ID);
        if (modContainer.isEmpty()) {
            modContainer = FabricLoader.getInstance().getModContainer(YesSteveModel.OoO0O0oO00O0o0OOOOoOOooo);
        }
        return modContainer;
    }

    private static void runYsmParser(Path executable, Path sourceFile, Path target) throws IOException, InterruptedException {
        Path tempRoot = Files.createTempDirectory("ysmparser-import-");
        try {
            Path inputRoot = tempRoot.resolve("input");
            Path outputRoot = tempRoot.resolve("output");
            Files.createDirectories(inputRoot);
            Files.createDirectories(outputRoot);

            Path copiedSource = inputRoot.resolve(sourceFile.getFileName().toString());
            Files.copy(sourceFile, copiedSource, StandardCopyOption.REPLACE_EXISTING);

            ProcessResult result = runYsmParserProcess(executable, inputRoot, outputRoot);
            Path exported = resolveYsmParserOutput(outputRoot, sourceFile);
            if (exported == null || !Files.exists(exported)) {
                throw new IOException("YSMParser did not produce a pack folder.\n" + String.join("\n", result.outputLines));
            }
            Files.move(exported, target, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            deleteRecursively(tempRoot);
        }
    }

    private static ProcessResult runYsmParserProcess(Path executable, Path inputRoot, Path outputRoot) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            executable.toString(),
            "-i",
            inputRoot.toString(),
            "-o",
            outputRoot.toString(),
            "-v",
            "-j",
            "1"
        )
            .directory(executable.getParent().toFile())
            .redirectErrorStream(true);
        prependNativeLibraryPath(processBuilder, executable.getParent());

        Process process = processBuilder.start();
        List<String> outputLines = new ArrayList<>();
        try (InputStream in = process.getInputStream()) {
            new String(in.readAllBytes()).lines().forEach(outputLines::add);
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("YSMParser failed with exit code " + exitCode + "\n" + String.join("\n", outputLines));
        }
        return new ProcessResult(outputLines);
    }

    private static void prependNativeLibraryPath(ProcessBuilder processBuilder, Path libraryRoot) {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String variable = osName.contains("win") ? "PATH" : (osName.contains("mac") ? "DYLD_LIBRARY_PATH" : "LD_LIBRARY_PATH");
        String existing = processBuilder.environment().getOrDefault(variable, "");
        String updated = libraryRoot.toString() + java.io.File.pathSeparator + existing;
        processBuilder.environment().put(variable, updated);
        if (!variable.equals("PATH")) {
            String path = processBuilder.environment().getOrDefault("PATH", "");
            processBuilder.environment().put("PATH", libraryRoot.toString() + java.io.File.pathSeparator + path);
        }
    }

    private static Path resolveYsmParserOutput(Path outputRoot, Path sourceFile) throws IOException {
        if (Files.exists(outputRoot.resolve("ysm.json"))) {
            return outputRoot;
        }

        String sourceStem = sourceFile.getFileName().toString().replaceFirst("(?i)\\.ysm$", "");
        try (var stream = Files.list(outputRoot)) {
            List<Path> directories = stream
                .filter(Files::isDirectory)
                .sorted(Comparator.comparing(path -> path.getFileName().toString().toLowerCase(Locale.ROOT)))
                .toList();
            for (Path directory : directories) {
                if (directory.getFileName().toString().equalsIgnoreCase(sourceStem)) {
                    return directory;
                }
            }
            return directories.size() == 1 ? directories.get(0) : null;
        }
    }

    private static Path resolveYsmParserExecutableInBundle(Path bundleRoot) {
        if (bundleRoot == null || !Files.isDirectory(bundleRoot)) {
            return null;
        }

        Path executable = bundleRoot.resolve(ysmParserExecutableName());
        if (Files.isRegularFile(executable)) {
            return executable;
        }
        return null;
    }

    private static Path findExecutableOnPath(String executableName) {
        String path = System.getenv("PATH");
        if (path == null || path.isBlank()) {
            return null;
        }

        for (String segment : path.split(Pattern.quote(java.io.File.pathSeparator))) {
            if (segment == null || segment.isBlank()) {
                continue;
            }
            Path candidate = Path.of(segment).resolve(executableName);
            if (isUsableYsmParserExecutable(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean isUsableYsmParserExecutable(Path candidate) {
        return candidate != null && Files.isRegularFile(candidate);
    }

    private static String ysmParserExecutableName() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win") ? "YSMParser.exe" : "YSMParser";
    }

    private static String ysmParserPlatform() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            return "windows";
        }
        if (osName.contains("mac")) {
            return "macos";
        }
        if (osName.contains("linux")) {
            return "linux";
        }
        return null;
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

    private static Path resolveExportedFolder(Path baseDir, List<String> lines) {
        return resolveDumpFolder(baseDir, lines);
    }

    private static Optional<String> firstMatch(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? Optional.ofNullable(matcher.group(1)).map(String::trim) : Optional.empty();
    }

    private static String importDetails(Path sourceFile, String importBackend) {
        if (importBackend == null || importBackend.isBlank()) {
            return "Imported from " + sourceFile.getFileName();
        }
        return "Imported from " + sourceFile.getFileName() + " via " + importBackend;
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
