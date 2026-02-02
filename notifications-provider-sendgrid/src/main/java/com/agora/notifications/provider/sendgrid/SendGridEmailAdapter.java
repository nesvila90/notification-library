package com.agora.notifications.provider.sendgrid;

import com.agora.notifications.api.EmailMessage;
import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.Bytes;
import com.agora.notifications.core.ProviderAdapter;
import com.agora.notifications.core.ProviderConfig;
import com.agora.notifications.core.ProviderRequest;

import java.util.Map;

public final class SendGridEmailAdapter implements ProviderAdapter<EmailMessage> {
    @Override
    public ProviderId id() {
        return new ProviderId("sendgrid");
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
        SendGridConfig cfg = (SendGridConfig) c;
        String url = "https://api.sendgrid.com/v3/mail/send";
        String body = "{\"from\":{\"email\":\"" + esc(cfg.fromEmail()) + "\"},"
                + "\"personalizations\":[{\"to\":[{\"email\":\"" + esc(m.toEmail()) + "\"}]}],"
                + "\"subject\":\"" + esc(m.subject()) + "\","
                + "\"content\":[{\"type\":\"text/plain\",\"value\":\"" + esc(m.body()) + "\"}]"
                + "}";
        return new ProviderRequest(id(), "POST", url,
                Map.of("Authorization", "Bearer " + cfg.apiKey(), "Content-Type", "application/json", "Idempotency-Key", m.metadata().correlationId()),
                Bytes.utf8(body), java.time.Duration.ofSeconds(3));
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
