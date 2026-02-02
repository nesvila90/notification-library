package com.agora.notifications.core;

import com.agora.notifications.api.ProviderId;

public interface ProviderErrorMapper {
    ProviderId id();

    ProviderOutcome map(ProviderResponse response);

    ProviderOutcome map(NetFailure failure);
}
