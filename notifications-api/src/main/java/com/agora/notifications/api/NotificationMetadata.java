package com.agora.notifications.api;

import java.time.Instant;
import java.util.Map;

public record NotificationMetadata(String correlationId, Instant createdAt, Map<String, String> tags) {
    public NotificationMetadata {
        if (correlationId == null || correlationId.isBlank())
            throw new IllegalArgumentException("correlationId required");
        if (createdAt == null) throw new IllegalArgumentException("createdAt required");
        tags = tags == null ? Map.of() : Map.copyOf(tags);
    }
}
