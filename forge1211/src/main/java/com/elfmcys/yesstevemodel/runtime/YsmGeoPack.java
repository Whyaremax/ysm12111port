package com.elfmcys.yesstevemodel.runtime;

import java.util.Set;
import net.minecraft.resources.Identifier;

public record YsmGeoPack(
    YsmCompiledPack compiledPack,
    Identifier modelResource,
    Identifier textureResource,
    Identifier animationResource,
    Set<String> animationNames,
    YsmScaleProfile scaleProfile,
    Set<String> firstPersonHiddenBones
) {
    public YsmGeoPack {
        animationNames = Set.copyOf(animationNames);
        firstPersonHiddenBones = Set.copyOf(firstPersonHiddenBones);
    }

    public boolean hasAnimation(String animationName) {
        return this.animationNames.contains(animationName);
    }
}
