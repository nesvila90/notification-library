package com.agora.notifications.provider.webpush;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.PushMessage;
import com.agora.notifications.api.PushTarget;
import com.agora.notifications.core.Bytes;
import com.agora.notifications.core.ProviderAdapter;
import com.agora.notifications.core.ProviderConfig;
import com.agora.notifications.core.ProviderRequest;

import java.util.Base64;
import java.util.Map;

public final class WebPushAdapter implements ProviderAdapter<PushMessage> {
    @Override
    public ProviderId id() {
        return new ProviderId("webpush");
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
        WebPushConfig cfg = (WebPushConfig) c;
        PushTarget.WebPushSubscription sub = (m.target() instanceof PushTarget.WebPushSubscription ws) ? ws : new PushTarget.WebPushSubscription("INVALID", "", "");
        String url = sub.endpoint();
        String payload = "{\"title\":\"" + esc(m.title()) + "\",\"body\":\"" + esc(m.body()) + "\"}";
        byte[] body = Base64.getEncoder().encode(Bytes.utf8(payload));
        return new ProviderRequest(id(), "POST", url,
                Map.of("TTL", "60", "Content-Type", "application/octet-stream", "Authorization", "vapid t=header.payload.signature, k=" + cfg.vapidPublicKey(), "Idempotency-Key", m.metadata().correlationId()),
                body, java.time.Duration.ofSeconds(3));
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
