package com.agora.notifications.core;

import java.time.Duration;

public record CircuitBreakerPolicy(int failureThreshold, Duration openDuration) {
    public CircuitBreakerPolicy {
        if (failureThreshold < 1) throw new IllegalArgumentException("failureThreshold must be >= 1");
        openDuration = openDuration == null ? Duration.ofSeconds(15) : openDuration;
    }
}
