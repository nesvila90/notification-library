package com.agora.notifications.provider.fcm;

import com.agora.notifications.api.Builders;
import com.agora.notifications.api.ProviderId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FcmAdapterTest {
    @Test
    void buildsHttpV1Url() {
        var cfg = FcmConfig.builder().projectId("p1").accessToken("t").build();
        var msg = Builders.push().deviceToken("tok").title("t").body("b").build();
        var req = new FcmPushAdapter().toRequest(msg, cfg);
        assertTrue(req.url().contains("/v1/projects/p1/messages:send"));
        assertEquals(new ProviderId("fcm"), req.providerId());
    }
}
