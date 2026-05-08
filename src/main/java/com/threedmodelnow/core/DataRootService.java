package com.threedmodelnow.core;

import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class DataRootService {
    private DataRootService() {
    }

    public static Path root() {
        return FabricLoader.getInstance().getConfigDir().resolve("3dmodelnow");
    }

    public static Path legacyYsmRoot() {
        return FabricLoader.getInstance().getConfigDir().resolve(ThreeDModelNow.LEGACY_YSM_NAMESPACE);
    }

    public static Path ysmCompatRoot() {
        return root().resolve("ysm");
    }
}
