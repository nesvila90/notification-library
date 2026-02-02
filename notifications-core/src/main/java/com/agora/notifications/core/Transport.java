package com.agora.notifications.core;

import java.util.concurrent.CompletionStage;

public interface Transport {
    CompletionStage<ProviderResponse> execute(ProviderRequest request);
}
