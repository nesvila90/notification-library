package com.agora.notifications.api;

public record EmailMessage(NotificationMetadata metadata, String toEmail, String subject,
                           String body) implements NotificationMessage {
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }
}
