package com.elfmcys.yesstevemodel.runtime;

import java.util.Set;
import net.minecraft.util.Identifier;

public record YsmGeoPack(
    YsmCompiledPack compiledPack,
    Identifier modelResource,
    Identifier textureResource,
    Identifier animationResource,
    Set<String> animationNames,
    YsmScaleProfile scaleProfile
) {
    public YsmGeoPack {
        animationNames = Set.copyOf(animationNames);
    }

    public boolean hasAnimation(String animationName) {
        return this.animationNames.contains(animationName);
    }
}
