package com.agora.notifications.core;

import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;

import java.util.HashMap;
import java.util.Map;

public final class NotificationsConfig {
    private final Map<NotificationChannel, ChannelPolicy> policies;
    private final Map<ProviderId, ProviderConfig> providerConfigs;

    private NotificationsConfig(Builder b) {
        this.policies = Map.copyOf(b.policies);
        this.providerConfigs = Map.copyOf(b.providerConfigs);
    }

    public ChannelPolicy policy(NotificationChannel ch) {
        return policies.get(ch);
    }

    public ProviderConfig providerConfig(ProviderId id) {
        return providerConfigs.get(id);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<NotificationChannel, ChannelPolicy> policies = new HashMap<>();
        private final Map<ProviderId, ProviderConfig> providerConfigs = new HashMap<>();

        public Builder() {
            for (NotificationChannel ch : NotificationChannel.values())
                policies.put(ch, new ChannelPolicy(ch, new ProviderId("undefined"), false));
        }

        public Builder enable(NotificationChannel ch, ProviderId provider) {
            policies.put(ch, new ChannelPolicy(ch, provider, true));
            return this;
        }

        public Builder providerConfig(ProviderConfig cfg) {
            providerConfigs.put(cfg.providerId(), cfg);
            return this;
        }

        public NotificationsConfig build() {
            return new NotificationsConfig(this);
        }
    }
}
