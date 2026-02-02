package com.agora.notifications.core;

public interface RetryDecider {
    boolean shouldRetry(int attempt, NetFailure failure, ProviderResponse resp);
}