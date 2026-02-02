package com.agora.notifications.provider.fcm;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.PushMessage;
import com.agora.notifications.api.PushTarget;
import com.agora.notifications.core.Bytes;
import com.agora.notifications.core.ProviderAdapter;
import com.agora.notifications.core.ProviderConfig;
import com.agora.notifications.core.ProviderRequest;

import java.util.Map;

public final class FcmPushAdapter implements ProviderAdapter<PushMessage> {
    @Override
    public ProviderId id() {
        return new ProviderId("fcm");
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
        FcmConfig cfg = (FcmConfig) c;
        String url = "https://fcm.googleapis.com/v1/projects/" + cfg.projectId() + "/messages:send";
        String token = (m.target() instanceof PushTarget.DeviceToken dt) ? dt.token() : "INVALID";
        String body = "{\"message\":{\"token\":\"" + esc(token) + "\",\"notification\":{\"title\":\"" + esc(m.title()) + "\",\"body\":\"" + esc(m.body()) + "\"},\"data\":{}}}";
        return new ProviderRequest(id(), "POST", url,
                Map.of("Authorization", "Bearer " + cfg.accessToken(), "Content-Type", "application/json", "Idempotency-Key", m.metadata().correlationId()),
                Bytes.utf8(body), java.time.Duration.ofSeconds(3));
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
