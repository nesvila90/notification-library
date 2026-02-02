package com.agora.notifications.provider.mailgun;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.ProviderConfig;

public record MailgunConfig(ProviderId providerId, String apiKey, String domain) implements ProviderConfig {
    public MailgunConfig {
        if (apiKey == null || apiKey.isBlank()) throw new IllegalArgumentException("apiKey required");
        if (domain == null || domain.isBlank()) throw new IllegalArgumentException("domain required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String apiKey, domain;

        public Builder apiKey(String v) {
            apiKey = v;
            return this;
        }

        public Builder domain(String v) {
            domain = v;
            return this;
        }

        public MailgunConfig build() {
            return new MailgunConfig(new ProviderId("mailgun"), apiKey, domain);
        }
    }
}
