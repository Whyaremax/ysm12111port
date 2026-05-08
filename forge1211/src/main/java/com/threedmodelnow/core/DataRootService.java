package com.threedmodelnow.core;

import java.nio.file.Path;
import com.threedmodelnow.forge1211.ForgePlatform;

public final class DataRootService {
    private DataRootService() {
    }

    public static Path root() {
        return ForgePlatform.configDir().resolve("3dmodelnow");
    }

    public static Path legacyYsmRoot() {
        return ForgePlatform.configDir().resolve(ThreeDModelNow.LEGACY_YSM_NAMESPACE);
    }

    public static Path ysmCompatRoot() {
        return root().resolve("ysm");
    }
}
