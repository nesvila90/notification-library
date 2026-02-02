package com.agora.notifications.provider.mailgun;

import com.agora.notifications.api.EmailMessage;
import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.Bytes;
import com.agora.notifications.core.ProviderAdapter;
import com.agora.notifications.core.ProviderConfig;
import com.agora.notifications.core.ProviderRequest;

import java.util.Map;

public final class MailgunEmailAdapter implements ProviderAdapter<EmailMessage> {
    @Override
    public ProviderId id() {
        return new ProviderId("mailgun");
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public Class<EmailMessage> messageType() {
        return EmailMessage.class;
    }

    @Override
    public ProviderRequest toRequest(EmailMessage m, ProviderConfig c) {
        MailgunConfig cfg = (MailgunConfig) c;
        String url = "https://api.mailgun.net/v3/" + cfg.domain() + "/messages";
        String body = "from=noreply@" + cfg.domain() + "&to=" + enc(m.toEmail()) + "&subject=" + enc(m.subject()) + "&text=" + enc(m.body());
        return new ProviderRequest(id(), "POST", url,
                Map.of("Authorization", "Basic " + cfg.apiKey(), "Content-Type", "application/x-www-form-urlencoded", "Idempotency-Key", m.metadata().correlationId()),
                Bytes.utf8(body), java.time.Duration.ofSeconds(3));
    }

    private static String enc(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
