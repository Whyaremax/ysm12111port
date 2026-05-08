package com.elfmcys.yesstevemodel.runtime;

public record YsmActiveSelection(
    YsmCompiledPack compiledPack,
    YsmGeoPack geoPack,
    boolean runtimeApplied
) {
}
