package com.agora.notifications.api;

public sealed interface PushTarget permits PushTarget.DeviceToken, PushTarget.WebPushSubscription {
    record DeviceToken(String token) implements PushTarget {
    }

    record WebPushSubscription(String endpoint, String p256dh, String auth) implements PushTarget {
    }
}
