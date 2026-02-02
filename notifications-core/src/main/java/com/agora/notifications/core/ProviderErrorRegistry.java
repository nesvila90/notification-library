package com.agora.notifications.core;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.UnknownProviderException;

import java.util.HashMap;
import java.util.Map;

public final class ProviderErrorRegistry {
    private final Map<ProviderId, ProviderErrorMapper> mappers = new HashMap<>();

    public ProviderErrorRegistry register(ProviderErrorMapper m) {
        mappers.put(m.id(), m);
        return this;
    }

    public ProviderErrorMapper get(ProviderId id) {
        ProviderErrorMapper m = mappers.get(id);
        if (m == null) throw new UnknownProviderException(id.value());
        return m;
    }
}
