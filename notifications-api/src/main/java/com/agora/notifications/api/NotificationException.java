package com.agora.notifications.api;

public sealed class NotificationException extends RuntimeException permits ValidationException, ChannelDisabledException, UnknownProviderException, SendFailedException {
    public final String code;

    protected NotificationException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    protected NotificationException(String code, String msg, Throwable c) {
        super(msg, c);
        this.code = code;
    }
}
