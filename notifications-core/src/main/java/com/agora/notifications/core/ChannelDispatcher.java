package com.agora.notifications.core;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.NotificationMessage;
import com.agora.notifications.api.NotificationReceipt;

import java.util.concurrent.CompletionStage;

public interface ChannelDispatcher<T extends NotificationMessage> {
    NotificationChannel channel();

    NotificationReceipt send(T message, NotificationContext ctx);

    CompletionStage<NotificationReceipt> sendAsync(T message, NotificationContext ctx);
}
