package com.agora.notifications.core;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;

public record ChannelPolicy(NotificationChannel channel, ProviderId activeProvider, boolean enabled) {
}