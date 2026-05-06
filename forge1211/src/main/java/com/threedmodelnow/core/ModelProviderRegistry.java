package com.threedmodelnow.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModelProviderRegistry {
    private static final Map<String, ModelFormatProvider> FORMAT_PROVIDERS = new LinkedHashMap<>();
    private static final Map<String, ModelImportProvider> IMPORT_PROVIDERS = new LinkedHashMap<>();
    private static final Map<String, ModelDisplayProvider> DISPLAY_PROVIDERS = new LinkedHashMap<>();

    private ModelProviderRegistry() {
    }

    public static synchronized void registerFormatProvider(ModelFormatProvider provider) {
        FORMAT_PROVIDERS.put(provider.id(), provider);
    }

    public static synchronized void registerImportProvider(ModelImportProvider provider) {
        IMPORT_PROVIDERS.put(provider.id(), provider);
    }

    public static synchronized void registerDisplayProvider(ModelDisplayProvider provider) {
        DISPLAY_PROVIDERS.put(provider.id(), provider);
    }

    public static synchronized List<ModelFormatProvider> formatProviders() {
        return List.copyOf(FORMAT_PROVIDERS.values());
    }

    public static synchronized List<ModelImportProvider> importProviders() {
        return List.copyOf(IMPORT_PROVIDERS.values());
    }

    public static synchronized List<ModelDisplayProvider> displayProviders() {
        return List.copyOf(DISPLAY_PROVIDERS.values());
    }

    public static synchronized List<String> providerIds() {
        List<String> ids = new ArrayList<>();
        ids.addAll(FORMAT_PROVIDERS.keySet());
        ids.addAll(IMPORT_PROVIDERS.keySet());
        ids.addAll(DISPLAY_PROVIDERS.keySet());
        return List.copyOf(ids);
    }
}
