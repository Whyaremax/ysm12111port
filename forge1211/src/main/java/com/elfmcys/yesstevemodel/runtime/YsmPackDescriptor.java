package com.elfmcys.yesstevemodel.runtime;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public record YsmPackDescriptor(
    String id,
    String legacyModelId,
    String displayName,
    String defaultTextureId,
    SourceType sourceType,
    Path rootPath,
    String formatLabel,
    String details,
    List<String> textureIds,
    boolean hasMainModel,
    boolean hasArmModel,
    boolean hasFpArmAnimation
) {
    public YsmPackDescriptor {
        id = Objects.requireNonNull(id, "id");
        legacyModelId = Objects.requireNonNull(legacyModelId, "legacyModelId");
        displayName = Objects.requireNonNull(displayName, "displayName");
        defaultTextureId = Objects.requireNonNullElse(defaultTextureId, "default");
        sourceType = Objects.requireNonNull(sourceType, "sourceType");
        rootPath = Objects.requireNonNull(rootPath, "rootPath");
        formatLabel = Objects.requireNonNullElse(formatLabel, "builtin");
        details = Objects.requireNonNullElse(details, "");
        textureIds = List.copyOf(textureIds == null || textureIds.isEmpty() ? List.of(defaultTextureId) : textureIds);
    }

    public enum SourceType {
        BUILTIN,
        IMPORTED
    }
}
