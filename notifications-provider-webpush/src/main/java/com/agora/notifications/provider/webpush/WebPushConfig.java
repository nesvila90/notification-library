package com.agora.notifications.provider.webpush;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.ProviderConfig;

public record WebPushConfig(ProviderId providerId, String vapidPublicKey, String vapidPrivateKey,
                            String subject) implements ProviderConfig {
    public WebPushConfig {
        if (vapidPublicKey == null || vapidPublicKey.isBlank())
            throw new IllegalArgumentException("vapidPublicKey required");
        if (vapidPrivateKey == null || vapidPrivateKey.isBlank())
            throw new IllegalArgumentException("vapidPrivateKey required");
        if (subject == null || subject.isBlank()) throw new IllegalArgumentException("subject required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String pub, priv, sub;

        public Builder vapidPublicKey(String v) {
            pub = v;
            return this;
        }

        public Builder vapidPrivateKey(String v) {
            priv = v;
            return this;
        }

        public Builder subject(String v) {
            sub = v;
            return this;
        }

        public WebPushConfig build() {
            return new WebPushConfig(new ProviderId("webpush"), pub, priv, sub);
        }
    }
}
