package com.threedmodelnow.fabric1211;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModelBrowserRegistry {
    private static final Map<String, ModelBrowserProvider> PROVIDERS = new LinkedHashMap<>();

    private ModelBrowserRegistry() {
    }

    public static synchronized void register(ModelBrowserProvider provider) {
        PROVIDERS.put(provider.id(), provider);
    }

    public static synchronized List<ModelBrowserProvider> providers() {
        return List.copyOf(PROVIDERS.values());
    }
}
