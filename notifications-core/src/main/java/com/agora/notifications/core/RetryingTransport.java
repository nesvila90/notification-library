package com.agora.notifications.core;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class RetryingTransport implements Transport {
    private final Transport next;
    private final RetryPolicy policy;
    private final RetryDecider decider;
    private final HttpExceptionMapper exMapper;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "retry-scheduler");
        t.setDaemon(true);
        return t;
    });

    public RetryingTransport(Transport next, RetryPolicy policy, RetryDecider decider, HttpExceptionMapper exMapper) {
        this.next = next;
        this.policy = policy;
        this.decider = decider;
        this.exMapper = exMapper;
    }

    @Override
    public CompletionStage<ProviderResponse> execute(ProviderRequest req) {
        return attempt(req, 1);
    }

    private CompletionStage<ProviderResponse> attempt(ProviderRequest req, int attempt) {
        return next.execute(req).handle((resp, err) -> new Outcome(resp, err)).thenCompose(out -> {
            NetFailure f = out.err == null ? null : exMapper.map(out.err);
            if (!decider.shouldRetry(attempt, f, out.resp) || attempt >= policy.maxAttempts()) {
                if (out.err != null) return CompletableFuture.failedFuture(out.err);
                return CompletableFuture.completedFuture(out.resp);
            }
            Duration d = backoff(attempt);
            CompletableFuture<Void> wait = new CompletableFuture<>();
            scheduler.schedule(() -> wait.complete(null), d.toMillis(), TimeUnit.MILLISECONDS);
            return wait.thenCompose(v -> attempt(req, attempt + 1));
        });
    }

    private Duration backoff(int attempt) {
        long base = policy.baseDelay().toMillis();
        long exp = base * (1L << Math.max(0, attempt - 1));
        long capped = Math.min(exp, policy.maxDelay().toMillis());
        double jitter = 1.0 + ((ThreadLocalRandom.current().nextDouble() * 2 - 1) * policy.jitterRatio());
        return Duration.ofMillis(Math.max(0, (long) (capped * jitter)));
    }

    private record Outcome(ProviderResponse resp, Throwable err) {
    }
}
