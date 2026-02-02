package com.agora.notifications.core;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public final class CircuitBreakerTransport implements Transport {
    private final Transport next;
    private final CircuitBreakerPolicy policy;
    private final ConcurrentHashMap<String, Circuit> circuits = new ConcurrentHashMap<>();

    public CircuitBreakerTransport(Transport next, CircuitBreakerPolicy policy) {
        this.next = next;
        this.policy = policy;
    }

    @Override
    public CompletionStage<ProviderResponse> execute(ProviderRequest req) {
        Circuit c = circuits.computeIfAbsent(req.providerId().value(), k -> new Circuit());
        if (c.isOpen()) return CompletableFuture.failedFuture(new RuntimeException("circuit_open"));
        return next.execute(req).whenComplete((resp, err) -> {
            boolean failure = err != null || isRetryable(resp);
            if (failure) c.onFailure(policy.failureThreshold(), policy.openDuration());
            else c.onSuccess();
        });
    }

    private static boolean isRetryable(ProviderResponse r) {
        if (r == null) return true;
        int s = r.httpStatus();
        return s == 408 || s == 429 || (s >= 500 && s <= 504);
    }

    private static final class Circuit {
        private int failures = 0;
        private volatile long openUntil = 0;

        boolean isOpen() {
            return System.currentTimeMillis() < openUntil;
        }

        synchronized void onFailure(int th, Duration d) {
            failures++;
            if (failures >= th) openUntil = System.currentTimeMillis() + d.toMillis();
        }

        synchronized void onSuccess() {
            failures = 0;
            openUntil = 0;
        }
    }
}
