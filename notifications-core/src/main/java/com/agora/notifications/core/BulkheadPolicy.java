package com.agora.notifications.core;

public record BulkheadPolicy(int maxConcurrentPerProvider) {
    public BulkheadPolicy {
        if (maxConcurrentPerProvider < 1) throw new IllegalArgumentException();
    }
}