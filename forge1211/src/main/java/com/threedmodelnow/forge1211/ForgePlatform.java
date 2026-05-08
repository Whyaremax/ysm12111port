package com.threedmodelnow.forge1211;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

public final class ForgePlatform {
    private static final Map<URI, FileSystem> ZIP_FILE_SYSTEMS = new ConcurrentHashMap<>();

    private ForgePlatform() {
    }

    public static Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path gameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static Optional<Path> findResource(String resourcePath) {
        URL resource = ForgePlatform.class.getClassLoader().getResource(resourcePath);
        if (resource == null) {
            return Optional.empty();
        }

        try {
            if ("file".equals(resource.getProtocol())) {
                return Optional.of(Path.of(resource.toURI()));
            }
            if ("jar".equals(resource.getProtocol())) {
                return Optional.of(pathFromJarUrl(resource));
            }
        } catch (IOException | URISyntaxException exception) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static Path pathFromJarUrl(URL resource) throws IOException, URISyntaxException {
        String spec = resource.toURI().toString();
        int separator = spec.indexOf("!/");
        if (separator < 0) {
            throw new IOException("Malformed jar resource URL: " + spec);
        }

        URI jarUri = URI.create(spec.substring(0, separator));
        String internalPath = spec.substring(separator + 1);
        FileSystem fileSystem = ZIP_FILE_SYSTEMS.computeIfAbsent(jarUri, uri -> {
            try {
                return FileSystems.newFileSystem(uri, Map.of());
            } catch (IOException exception) {
                throw new IllegalStateException(exception);
            }
        });
        return fileSystem.getPath(internalPath);
    }
}
