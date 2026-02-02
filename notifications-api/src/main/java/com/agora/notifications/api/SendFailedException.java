package com.agora.notifications.api;

public final class SendFailedException extends NotificationException {
    public SendFailedException(String msg, Throwable c) {
        super("send_failed", msg, c);
    }
}
