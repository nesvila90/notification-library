package com.agora.notifications.core;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.NotificationMessage;
import com.agora.notifications.api.ProviderId;

public interface ProviderAdapter<T extends NotificationMessage> {
    ProviderId id();

    NotificationChannel channel();

    Class<T> messageType();

    ProviderRequest toRequest(T message, ProviderConfig config);
}
