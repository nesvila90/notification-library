package com.agora.notifications.core;

import java.util.Map;

public record ProviderResponse(int httpStatus, Map<String, String> headers, byte[] body) {
    public ProviderResponse {
        headers = headers == null ? Map.of() : Map.copyOf(headers);
        body = body == null ? new byte[0] : body;
    }

    public String bodyUtf8() {
        return new String(body, java.nio.charset.StandardCharsets.UTF_8);
    }
}
