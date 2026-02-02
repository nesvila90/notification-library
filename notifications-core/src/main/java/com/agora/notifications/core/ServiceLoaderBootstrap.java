package com.agora.notifications.core;

import java.util.ServiceLoader;

public final class ServiceLoaderBootstrap {
    private ServiceLoaderBootstrap() {
    }

    public static ProviderAdapterRegistry loadAdapters() {
        ProviderAdapterRegistry r = new ProviderAdapterRegistry();
        ServiceLoader.load(ProviderAdapter.class).forEach(r::register);
        return r;
    }

    public static ProviderErrorRegistry loadErrorMappers() {
        ProviderErrorRegistry r = new ProviderErrorRegistry();
        ServiceLoader.load(ProviderErrorMapper.class).forEach(r::register);
        return r;
    }
}
