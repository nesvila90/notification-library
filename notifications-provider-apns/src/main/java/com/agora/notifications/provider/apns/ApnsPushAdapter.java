package com.agora.notifications.provider.apns;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.PushMessage;
import com.agora.notifications.api.PushTarget;
import com.agora.notifications.core.Bytes;
import com.agora.notifications.core.ProviderAdapter;
import com.agora.notifications.core.ProviderConfig;
import com.agora.notifications.core.ProviderRequest;

import java.util.Map;
import java.util.UUID;

public final class ApnsPushAdapter implements ProviderAdapter<PushMessage> {
    @Override
    public ProviderId id() {
        return new ProviderId("apns");
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public Class<PushMessage> messageType() {
        return PushMessage.class;
    }

    @Override
    public ProviderRequest toRequest(PushMessage m, ProviderConfig c) {
        ApnsConfig cfg = (ApnsConfig) c;
        String token = (m.target() instanceof PushTarget.DeviceToken dt) ? dt.token() : "INVALID";
        String url = "https://api.push.apple.com/3/device/" + token;
        String body = "{\"aps\":{\"alert\":{\"title\":\"" + esc(m.title()) + "\",\"body\":\"" + esc(m.body()) + "\"}}}";
        return new ProviderRequest(id(), "POST", url,
                Map.of("authorization", "bearer " + cfg.authToken(), "apns-topic", cfg.topic(), "apns-push-type", "alert", "apns-priority", "10", "apns-id", UUID.randomUUID().toString(),
                        "content-type", "application/json", "Idempotency-Key", m.metadata().correlationId()),
                Bytes.utf8(body), java.time.Duration.ofSeconds(3));
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
