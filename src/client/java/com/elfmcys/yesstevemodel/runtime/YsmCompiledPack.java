package com.elfmcys.yesstevemodel.runtime;

import java.util.List;

public record YsmCompiledPack(
    YsmPackDescriptor descriptor,
    YsmSourcePack sourcePack,
    String selectedTextureId,
    boolean renderableThirdPerson,
    boolean renderableFirstPerson,
    List<String> warnings
) {
    public YsmCompiledPack {
        warnings = List.copyOf(warnings);
    }

    public boolean isRenderable() {
        return this.renderableThirdPerson || this.renderableFirstPerson;
    }
}
