package com.agora.notifications.core;

import org.slf4j.Logger;

import java.util.concurrent.CompletionStage;

public final class ObservabilityTransport implements Transport {
    private final Transport next;
    private final Logger log;

    public ObservabilityTransport(Transport next, Logger log) {
        this.next = next;
        this.log = log;
    }

    @Override
    public CompletionStage<ProviderResponse> execute(ProviderRequest req) {
        long start = System.nanoTime();
        return next.execute(req).whenComplete((resp, err) -> {
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (err != null)
                log.warn("transport provider={} method={} url={} latencyMs={} err={}", req.providerId().value(), req.method(), req.url(), ms, err.toString());
            else
                log.debug("transport provider={} status={} latencyMs={}", req.providerId().value(), resp.httpStatus(), ms);
        });
    }
}
