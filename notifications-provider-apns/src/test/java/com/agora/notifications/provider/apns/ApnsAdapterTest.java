package com.agora.notifications.provider.apns;

import com.agora.notifications.api.Builders;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApnsAdapterTest {
    @Test
    void addsApnsHeaders() {
        var cfg = ApnsConfig.builder().topic("com.example.app").authToken("jwt").build();
        var msg = Builders.push().deviceToken("tok").title("t").body("b").build();
        var req = new ApnsPushAdapter().toRequest(msg, cfg);
        assertEquals("com.example.app", req.headers().get("apns-topic"));
        assertNotNull(req.headers().get("apns-id"));
    }
}
