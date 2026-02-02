package com.agora.notifications.core;

import com.agora.notifications.api.BatchReceipt;
import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.NotificationMessage;
import com.agora.notifications.api.NotificationReceipt;
import com.agora.notifications.api.Notifications;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class NotificationEngine implements Notifications {
    private final Map<NotificationChannel, ChannelDispatcher<?>> dispatchers;
    private final NotificationContext ctx;

    public NotificationEngine(Map<NotificationChannel, ChannelDispatcher<?>> dispatchers, NotificationContext ctx) {
        this.dispatchers = Map.copyOf(dispatchers);
        this.ctx = Objects.requireNonNull(ctx);
    }

    @Override
    public NotificationReceipt send(NotificationMessage message) {
        @SuppressWarnings("unchecked") ChannelDispatcher<NotificationMessage> d = (ChannelDispatcher<NotificationMessage>) dispatchers.get(message.channel());
        if (d == null) throw new IllegalStateException("No dispatcher for channel: " + message.channel());
        return d.send(message, ctx);
    }

    @Override
    public CompletionStage<NotificationReceipt> sendAsync(NotificationMessage message) {
        @SuppressWarnings("unchecked") ChannelDispatcher<NotificationMessage> d = (ChannelDispatcher<NotificationMessage>) dispatchers.get(message.channel());
        if (d == null)
            return CompletableFuture.failedFuture(new IllegalStateException("No dispatcher for channel: " + message.channel()));
        return d.sendAsync(message, ctx);
    }

    @Override
    public CompletionStage<BatchReceipt> sendBatchAsync(List<NotificationMessage> messages) {
        var futures = messages.stream().map(this::sendAsync).toList();
        CompletableFuture<?>[] all = futures.stream().map(CompletionStage::toCompletableFuture).toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(all).thenApply(v -> new BatchReceipt(futures.stream().map(CompletionStage::toCompletableFuture).map(CompletableFuture::join).toList()));
    }
}
