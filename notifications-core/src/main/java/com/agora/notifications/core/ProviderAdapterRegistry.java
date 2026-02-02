package com.agora.notifications.core;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.NotificationMessage;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.UnknownProviderException;

import java.util.HashMap;
import java.util.Map;

public final class ProviderAdapterRegistry {
    private final Map<Key, ProviderAdapter<?>> adapters = new HashMap<>();

    public ProviderAdapterRegistry register(ProviderAdapter<?> a) {
        adapters.put(new Key(a.id(), a.channel()), a);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends NotificationMessage> ProviderAdapter<T> get(ProviderId pid, NotificationChannel ch) {
        ProviderAdapter<?> a = adapters.get(new Key(pid, ch));
        if (a == null) throw new UnknownProviderException(pid.value());
        return (ProviderAdapter<T>) a;
    }

    private record Key(ProviderId providerId, NotificationChannel channel) {
    }
}
