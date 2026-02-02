package com.agora.notifications.core;

import java.util.regex.Pattern;

public final class Validation {
    private Validation() {
    }

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{7,14}$");

    public static boolean isEmail(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }

    public static boolean isE164(String s) {
        return s != null && E164.matcher(s).matches();
    }
}
