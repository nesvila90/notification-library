package com.agora.notifications.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public final class BulkheadTransport implements Transport {
    private final Transport next;
    private final BulkheadPolicy policy;
    private final ConcurrentHashMap<String, Semaphore> sems = new ConcurrentHashMap<>();

    public BulkheadTransport(Transport next, BulkheadPolicy policy) {
        this.next = next;
        this.policy = policy;
    }

    @Override
    public CompletionStage<ProviderResponse> execute(ProviderRequest req) {
        Semaphore sem = sems.computeIfAbsent(req.providerId().value(), k -> new Semaphore(policy.maxConcurrentPerProvider()));
        if (!sem.tryAcquire()) return CompletableFuture.failedFuture(new RuntimeException("bulkhead_rejected"));
        return next.execute(req).whenComplete((r, e) -> sem.release());
    }
}
