package com.agora.notifications.core;

import java.nio.charset.StandardCharsets;

public final class Bytes {
    private Bytes() {
    }

    public static byte[] utf8(String s) {
        return s == null ? new byte[0] : s.getBytes(StandardCharsets.UTF_8);
    }
}