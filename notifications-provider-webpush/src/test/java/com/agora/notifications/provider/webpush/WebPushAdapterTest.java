package com.agora.notifications.provider.webpush;

import com.agora.notifications.api.Builders;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebPushAdapterTest {
    @Test
    void setsVapidAuthorizationHeader() {
        var cfg = WebPushConfig.builder().vapidPublicKey("pk").vapidPrivateKey("sk").subject("mailto:x@y.com").build();
        var msg = Builders.push().webSubscription("https://endpoint", "p", "a").title("t").body("b").build();
        var req = new WebPushAdapter().toRequest(msg, cfg);
        assertTrue(req.headers().get("Authorization").startsWith("vapid "));
        assertEquals("60", req.headers().get("TTL"));
    }
}
