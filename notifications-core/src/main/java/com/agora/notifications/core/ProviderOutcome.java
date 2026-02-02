package com.agora.notifications.core;

public record ProviderOutcome(boolean success, String errorCode, String errorMessage, boolean retryableHint) {
    public static ProviderOutcome ok() {
        return new ProviderOutcome(true, null, null, false);
    }

    public static ProviderOutcome fail(String code, String msg, boolean retryable) {
        return new ProviderOutcome(false, code, msg, retryable);
    }
}
