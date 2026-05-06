package com.elfmcys.yesstevemodel.runtime;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Map;

public record YsmSourcePack(
    Path rootPath,
    JsonObject manifest,
    Path mainModelPath,
    JsonObject mainModel,
    Path armModelPath,
    JsonObject armModel,
    Path mainAnimationPath,
    JsonObject mainAnimation,
    Path armAnimationPath,
    JsonObject armAnimation,
    Path fpArmAnimationPath,
    JsonObject fpArmAnimation,
    Map<String, Path> textures,
    String selectedTextureId,
    Path selectedTexturePath
) {
    public YsmSourcePack {
        textures = Map.copyOf(textures);
    }

    public boolean hasMainModel() {
        return this.mainModelPath != null && this.mainModel != null;
    }

    public boolean hasArmModel() {
        return this.armModelPath != null && this.armModel != null;
    }

    public boolean hasMainAnimation() {
        return this.mainAnimationPath != null && this.mainAnimation != null;
    }

    public boolean hasArmAnimation() {
        return this.armAnimationPath != null && this.armAnimation != null;
    }

    public boolean hasFpArmAnimation() {
        return this.fpArmAnimationPath != null && this.fpArmAnimation != null;
    }

    public boolean hasSelectedTexture() {
        return this.selectedTexturePath != null;
    }
}
