package com.agora.notifications.provider.twilio;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.SmsMessage;
import com.agora.notifications.core.Bytes;
import com.agora.notifications.core.ProviderAdapter;
import com.agora.notifications.core.ProviderConfig;
import com.agora.notifications.core.ProviderRequest;

import java.util.Base64;
import java.util.Map;

public final class TwilioSmsAdapter implements ProviderAdapter<SmsMessage> {
    @Override
    public ProviderId id() {
        return new ProviderId("twilio");
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
        TwilioConfig cfg = (TwilioConfig) c;
        String url = "https://api.twilio.com/2010-04-01/Accounts/" + cfg.accountSid() + "/Messages.json";
        String basic = Base64.getEncoder().encodeToString((cfg.accountSid() + ":" + cfg.authToken()).getBytes());
        String body = "From=" + enc(cfg.fromPhoneE164()) + "&To=" + enc(m.phoneE164()) + "&Body=" + enc(m.body());
        return new ProviderRequest(id(), "POST", url,
                Map.of("Authorization", "Basic " + basic, "Content-Type", "application/x-www-form-urlencoded", "Idempotency-Key", m.metadata().correlationId()),
                Bytes.utf8(body), java.time.Duration.ofSeconds(3));
    }

    private static String enc(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
