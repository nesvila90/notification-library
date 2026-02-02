package com.agora.notifications.provider.apns;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.ProviderConfig;

public record ApnsConfig(ProviderId providerId, String topic, String authToken) implements ProviderConfig {
    public ApnsConfig {
        if (topic == null || topic.isBlank()) throw new IllegalArgumentException("topic required");
        if (authToken == null || authToken.isBlank()) throw new IllegalArgumentException("authToken required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String topic, tok;

        public Builder topic(String v) {
            topic = v;
            return this;
        }

        public Builder authToken(String v) {
            tok = v;
            return this;
        }

        public ApnsConfig build() {
            return new ApnsConfig(new ProviderId("apns"), topic, tok);
        }
    }
}
