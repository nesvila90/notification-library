package com.agora.notifications.core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public final class JavaHttpClientTransport implements Transport {
    private final HttpClient client;

    public JavaHttpClientTransport(HttpClient client) {
        this.client = client;
    }

    @Override
    public CompletionStage<ProviderResponse> execute(ProviderRequest req) {
        HttpRequest.Builder b = HttpRequest.newBuilder().uri(URI.create(req.url())).timeout(req.timeout());
        req.headers().forEach(b::header);
        HttpRequest httpReq = b.method(req.method(), HttpRequest.BodyPublishers.ofByteArray(req.body())).build();
        return client.sendAsync(httpReq, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(resp -> new ProviderResponse(resp.statusCode(),
                        resp.headers().map().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> String.join(",", e.getValue()))),
                        resp.body()));
    }
}
