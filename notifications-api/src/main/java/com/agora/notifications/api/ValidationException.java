package com.agora.notifications.api;

public final class ValidationException extends NotificationException {
    public ValidationException(String m) {
        super("validation_error", m);
    }
}
