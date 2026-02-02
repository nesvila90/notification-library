package com.agora.notifications.core;

import com.agora.notifications.api.ProviderId;

import java.time.Duration;
import java.util.Map;

public record ProviderRequest(ProviderId providerId, String method, String url, Map<String, String> headers,
                              byte[] body, Duration timeout) {
    public ProviderRequest {
        headers = headers == null ? Map.of() : Map.copyOf(headers);
        body = body == null ? new byte[0] : body;
        timeout = timeout == null ? Duration.ofSeconds(3) : timeout;
    }
}
