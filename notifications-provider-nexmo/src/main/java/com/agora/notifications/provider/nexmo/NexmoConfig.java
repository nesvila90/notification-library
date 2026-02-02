package com.agora.notifications.provider.nexmo;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.ProviderConfig;

public record NexmoConfig(ProviderId providerId, String apiKey, String apiSecret,
                          String from) implements ProviderConfig {
    public NexmoConfig {
        if (apiKey == null || apiKey.isBlank()) throw new IllegalArgumentException("apiKey required");
        if (apiSecret == null || apiSecret.isBlank()) throw new IllegalArgumentException("apiSecret required");
        if (from == null || from.isBlank()) throw new IllegalArgumentException("from required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String k, s, f;

        public Builder apiKey(String v) {
            k = v;
            return this;
        }

        public Builder apiSecret(String v) {
            s = v;
            return this;
        }

        public Builder from(String v) {
            f = v;
            return this;
        }

        public NexmoConfig build() {
            return new NexmoConfig(new ProviderId("nexmo"), k, s, f);
        }
    }
}
