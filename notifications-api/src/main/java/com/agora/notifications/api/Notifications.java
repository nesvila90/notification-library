package com.agora.notifications.api;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface Notifications {
    NotificationReceipt send(NotificationMessage message);

    CompletionStage<NotificationReceipt> sendAsync(NotificationMessage message);

    CompletionStage<BatchReceipt> sendBatchAsync(List<NotificationMessage> messages);
}
