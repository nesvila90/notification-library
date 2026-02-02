package com.agora.notifications.provider.twilio;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.ProviderConfig;

public record TwilioConfig(ProviderId providerId, String accountSid, String authToken,
                           String fromPhoneE164) implements ProviderConfig {
    public TwilioConfig {
        if (accountSid == null || accountSid.isBlank()) throw new IllegalArgumentException("accountSid required");
        if (authToken == null || authToken.isBlank()) throw new IllegalArgumentException("authToken required");
        if (fromPhoneE164 == null || fromPhoneE164.isBlank())
            throw new IllegalArgumentException("fromPhoneE164 required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String sid, tok, from;

        public Builder accountSid(String v) {
            sid = v;
            return this;
        }

        public Builder authToken(String v) {
            tok = v;
            return this;
        }

        public Builder fromPhoneE164(String v) {
            from = v;
            return this;
        }

        public TwilioConfig build() {
            return new TwilioConfig(new ProviderId("twilio"), sid, tok, from);
        }
    }
}
