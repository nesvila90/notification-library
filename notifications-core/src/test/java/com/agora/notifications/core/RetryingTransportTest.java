package com.agora.notifications.core;

import com.agora.notifications.api.ProviderId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetryingTransportTest {
    @Test
    void retriesOn503() {
        AtomicInteger calls = new AtomicInteger();
        Transport leaf = req -> {
            int n = calls.incrementAndGet();
            if (n < 3) return CompletableFuture.completedFuture(new ProviderResponse(503, Map.of(), "nope".getBytes()));
            return CompletableFuture.completedFuture(new ProviderResponse(200, Map.of(), "ok".getBytes()));
        };
        Transport t = new RetryingTransport(leaf, new RetryPolicy(5, Duration.ofMillis(1), Duration.ofMillis(5), 0.0),
                DefaultRetryDecider.INSTANCE, new DefaultHttpExceptionMapper());
        var resp = t.execute(new ProviderRequest(new ProviderId("x"), "POST", "http://x", Map.of(), "{}".getBytes(), Duration.ofMillis(50))).toCompletableFuture().join();
        assertEquals(200, resp.httpStatus());
        assertTrue(calls.get() >= 3);
    }

    @Test
    void doesNotRetryOn400() {
        AtomicInteger calls = new AtomicInteger();
        Transport leaf = req -> {
            calls.incrementAndGet();
            return CompletableFuture.completedFuture(new ProviderResponse(400, Map.of(), "bad".getBytes()));
        };
        Transport t = new RetryingTransport(leaf, new RetryPolicy(5, Duration.ofMillis(1), Duration.ofMillis(5), 0.0),
                DefaultRetryDecider.INSTANCE, new DefaultHttpExceptionMapper());
        var resp = t.execute(new ProviderRequest(new ProviderId("x"), "POST", "http://x", Map.of(), "{}".getBytes(), Duration.ofMillis(50))).toCompletableFuture().join();
        assertEquals(400, resp.httpStatus());
        assertEquals(1, calls.get());
    }
}
