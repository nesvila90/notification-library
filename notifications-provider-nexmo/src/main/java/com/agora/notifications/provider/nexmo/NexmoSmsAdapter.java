package com.agora.notifications.provider.nexmo;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.SmsMessage;
import com.agora.notifications.core.Bytes;
import com.agora.notifications.core.ProviderAdapter;
import com.agora.notifications.core.ProviderConfig;
import com.agora.notifications.core.ProviderRequest;

import java.util.Map;

public final class NexmoSmsAdapter implements ProviderAdapter<SmsMessage> {
    @Override
    public ProviderId id() {
        return new ProviderId("nexmo");
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }

    @Override
    public Class<SmsMessage> messageType() {
        return SmsMessage.class;
    }

    @Override
    public ProviderRequest toRequest(SmsMessage m, ProviderConfig c) {
        NexmoConfig cfg = (NexmoConfig) c;
        String url = "https://rest.nexmo.com/sms/json";
        String body = "{\"api_key\":\"" + esc(cfg.apiKey()) + "\",\"api_secret\":\"" + esc(cfg.apiSecret()) + "\",\"from\":\"" + esc(cfg.from()) + "\",\"to\":\"" + esc(m.phoneE164()) + "\",\"text\":\"" + esc(m.body()) + "\"}";
        return new ProviderRequest(id(), "POST", url, Map.of("Content-Type", "application/json", "Idempotency-Key", m.metadata().correlationId()),
                Bytes.utf8(body), java.time.Duration.ofSeconds(3));
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
