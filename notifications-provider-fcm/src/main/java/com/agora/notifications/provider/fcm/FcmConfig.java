package com.agora.notifications.provider.fcm;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.ProviderConfig;

public record FcmConfig(ProviderId providerId, String projectId, String accessToken) implements ProviderConfig {
    public FcmConfig {
        if (projectId == null || projectId.isBlank()) throw new IllegalArgumentException("projectId required");
        if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("accessToken required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String p, t;

        public Builder projectId(String v) {
            p = v;
            return this;
        }

        public Builder accessToken(String v) {
            t = v;
            return this;
        }

        public FcmConfig build() {
            return new FcmConfig(new ProviderId("fcm"), p, t);
        }
    }
}
