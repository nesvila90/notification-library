package com.agora.notifications.api;

public record SmsMessage(NotificationMetadata metadata, String phoneE164, String body) implements NotificationMessage {
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }
}
