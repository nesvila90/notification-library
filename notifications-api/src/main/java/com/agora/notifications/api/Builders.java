package com.agora.notifications.api;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class Builders {
    private Builders() {
    }

    private static NotificationMetadata meta(String cid) {
        return new NotificationMetadata(cid == null ? UUID.randomUUID().toString() : cid, Instant.now(), Map.of());
    }

    public static EmailBuilder email() {
        return new EmailBuilder();
    }

    public static SmsBuilder sms() {
        return new SmsBuilder();
    }

    public static PushBuilder push() {
        return new PushBuilder();
    }

    public static final class EmailBuilder {
        private String cid, to, sub, body;

        public EmailBuilder correlationId(String v) {
            cid = v;
            return this;
        }

        public EmailBuilder to(String v) {
            to = v;
            return this;
        }

        public EmailBuilder subject(String v) {
            sub = v;
            return this;
        }

        public EmailBuilder body(String v) {
            body = v;
            return this;
        }

        public EmailMessage build() {
            return new EmailMessage(meta(cid), to, sub, body);
        }
    }

    public static final class SmsBuilder {
        private String cid, phone, body;

        public SmsBuilder correlationId(String v) {
            cid = v;
            return this;
        }

        public SmsBuilder phoneE164(String v) {
            phone = v;
            return this;
        }

        public SmsBuilder body(String v) {
            body = v;
            return this;
        }

        public SmsMessage build() {
            return new SmsMessage(meta(cid), phone, body);
        }
    }

    public static final class PushBuilder {
        private String cid;
        private PushTarget target;
        private String title, body;
        private Map<String, String> data = Map.of();

        public PushBuilder correlationId(String v) {
            cid = v;
            return this;
        }

        public PushBuilder deviceToken(String t) {
            target = new PushTarget.DeviceToken(t);
            return this;
        }

        public PushBuilder webSubscription(String e, String p, String a) {
            target = new PushTarget.WebPushSubscription(e, p, a);
            return this;
        }

        public PushBuilder title(String v) {
            title = v;
            return this;
        }

        public PushBuilder body(String v) {
            body = v;
            return this;
        }

        public PushBuilder data(Map<String, String> v) {
            data = v;
            return this;
        }

        public PushMessage build() {
            return new PushMessage(meta(cid), target, title, body, data);
        }
    }
}
