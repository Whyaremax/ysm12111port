package com.elfmcys.yesstevemodel.runtime;

public final class YsmRenderStateStore {
    private static volatile YsmGeoPack activePack;
    private static volatile YsmGeoPlayerRenderer activeRenderer;

    private YsmRenderStateStore() {
    }

    public static synchronized void install(YsmGeoPack pack) {
        activePack = pack;
        activeRenderer = new YsmGeoPlayerRenderer(pack);
    }

    public static synchronized void clear() {
        activePack = null;
        activeRenderer = null;
    }

    public static YsmGeoPack activePack() {
        return activePack;
    }

    public static YsmGeoPlayerRenderer activeRenderer() {
        return activeRenderer;
    }

    public static boolean hasActivePack() {
        return activePack != null && activeRenderer != null;
    }
}
