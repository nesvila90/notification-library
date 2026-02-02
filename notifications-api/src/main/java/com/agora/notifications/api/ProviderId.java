package com.agora.notifications.api;

public record ProviderId(String value) {
    public ProviderId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("providerId cannot be blank");
    }
}
