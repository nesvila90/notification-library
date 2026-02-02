package com.agora.notifications.core;

import java.time.Duration;

public record RetryPolicy(int maxAttempts, Duration baseDelay, Duration maxDelay, double jitterRatio) {
    public RetryPolicy {
        if (maxAttempts < 1) throw new IllegalArgumentException("maxAttempts must be >= 1");
        baseDelay = baseDelay == null ? Duration.ofMillis(100) : baseDelay;
        maxDelay = maxDelay == null ? Duration.ofSeconds(2) : maxDelay;
        jitterRatio = Math.max(0.0, Math.min(1.0, jitterRatio));
    }
}
