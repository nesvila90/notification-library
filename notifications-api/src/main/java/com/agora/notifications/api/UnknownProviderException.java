package com.agora.notifications.api;

public final class UnknownProviderException extends NotificationException {
    public UnknownProviderException(String pid) {
        super("unknown_provider", "Unknown provider: " + pid);
    }
}
