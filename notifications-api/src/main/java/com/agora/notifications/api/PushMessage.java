package com.agora.notifications.api;

import java.util.Map;

public record PushMessage(NotificationMetadata metadata, PushTarget target, String title, String body,
                          Map<String, String> data)
        implements NotificationMessage {
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.PUSH;
    }

    public PushMessage {
        data = data == null ? Map.of() : Map.copyOf(data);
    }
}
