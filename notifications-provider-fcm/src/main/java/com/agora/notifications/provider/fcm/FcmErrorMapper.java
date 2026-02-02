package com.agora.notifications.provider.fcm;

import com.agora.notifications.api.ProviderId;
import com.agora.notifications.core.NetFailure;
import com.agora.notifications.core.ProviderErrorMapper;
import com.agora.notifications.core.ProviderOutcome;
import com.agora.notifications.core.ProviderResponse;

public final class FcmErrorMapper implements ProviderErrorMapper {
    @Override
    public ProviderId id() {
        return new ProviderId("fcm");
    }

    @Override
    public ProviderOutcome map(ProviderResponse r) {
        int s = r.httpStatus();
        if (s >= 200 && s < 300) return ProviderOutcome.ok();
        String body = r.bodyUtf8();
        boolean retryable = s == 429 || (s >= 500 && s <= 504) || body.contains("UNAVAILABLE");
        return ProviderOutcome.fail("fcm_http_" + s, body, retryable);
    }

    @Override
    public ProviderOutcome map(NetFailure f) {
        boolean retryable = switch (f.type()) {
            case TIMEOUT, DNS, CONNECT, IO -> true;
            default -> false;
        };
        return ProviderOutcome.fail("fcm_net_" + f.type().name().toLowerCase(), f.cause().toString(), retryable);
    }
}
