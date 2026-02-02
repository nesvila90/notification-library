package com.agora.notifications.core;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class MockTransport implements Transport {
    private final Random rnd;
    private final Duration minL;
    private final Duration maxL;

    public MockTransport(long seed, Duration minLatency, Duration maxLatency) {
        this.rnd = new Random(seed);
        this.minL = minLatency;
        this.maxL = maxLatency;
    }

    @Override
    public CompletionStage<ProviderResponse> execute(ProviderRequest req) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(latencyMs());
            String body = new String(req.body(), java.nio.charset.StandardCharsets.UTF_8);
            if (body.contains("TIMEOUT")) {
                sleep(req.timeout().toMillis() + 10);
                throw new RuntimeException(new java.net.http.HttpTimeoutException("simulated"));
            }
            if (body.contains("UNAUTHORIZED"))
                return new ProviderResponse(401, Map.of(), "{\"error\":\"unauthorized\"}".getBytes());
            if (body.contains("INVALID"))
                return new ProviderResponse(400, Map.of(), "{\"error\":\"invalid_argument\"}".getBytes());
            if (rnd.nextInt(100) < 10)
                return new ProviderResponse(503, Map.of("Retry-After", "1"), "{\"error\":\"unavailable\"}".getBytes());
            return new ProviderResponse(200, Map.of(), "{\"ok\":true}".getBytes());
        });
    }

    private long latencyMs() {
        long min = minL.toMillis();
        long max = Math.max(min + 1, maxL.toMillis());
        return min + rnd.nextInt((int) (max - min));
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
