package com.agora.notifications.provider.nexmo;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.NetFailure;
import com.agora.notifications.core.ProviderErrorMapper;
import com.agora.notifications.core.ProviderOutcome;
import com.agora.notifications.core.ProviderResponse;

public final class NexmoErrorMapper implements ProviderErrorMapper {
    @Override
    public ProviderId id() {
        return new ProviderId("nexmo");
    }

    @Override
    public ProviderOutcome map(ProviderResponse r) {
        int s = r.httpStatus();
        if (s >= 200 && s < 300) return ProviderOutcome.ok();
        boolean retryable = s == 429 || (s >= 500 && s <= 504);
        return ProviderOutcome.fail("nexmo_http_" + s, r.bodyUtf8(), retryable);
    }

    @Override
    public ProviderOutcome map(NetFailure f) {
        boolean retryable = switch (f.type()) {
            case TIMEOUT, DNS, CONNECT, IO -> true;
            default -> false;
        };
        return ProviderOutcome.fail("nexmo_net_" + f.type().name().toLowerCase(), f.cause().toString(), retryable);
    }
}
