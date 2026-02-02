package com.agora.notifications.provider.sendgrid;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.ProviderConfig;

public record SendGridConfig(ProviderId providerId, String apiKey, String fromEmail) implements ProviderConfig {
    public SendGridConfig {
        if (apiKey == null || apiKey.isBlank()) throw new IllegalArgumentException("apiKey required");
        if (fromEmail == null || fromEmail.isBlank()) throw new IllegalArgumentException("fromEmail required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String apiKey, fromEmail;

        public Builder apiKey(String v) {
            apiKey = v;
            return this;
        }

        public Builder fromEmail(String v) {
            fromEmail = v;
            return this;
        }

        public SendGridConfig build() {
            return new SendGridConfig(new ProviderId("sendgrid"), apiKey, fromEmail);
        }
    }
}
