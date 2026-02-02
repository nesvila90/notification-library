package com.agora.notifications.api;

public sealed interface NotificationMessage permits EmailMessage, SmsMessage, PushMessage {
    NotificationMetadata metadata();

    NotificationChannel channel();
}
