package com.agora.notifications.api;

public final class ChannelDisabledException extends NotificationException {
    public ChannelDisabledException(NotificationChannel ch) {
        super("channel_disabled", "Channel disabled: " + ch);
    }
}
