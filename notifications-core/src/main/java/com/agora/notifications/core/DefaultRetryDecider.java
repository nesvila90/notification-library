package com.agora.notifications.core;

public final class DefaultRetryDecider implements RetryDecider {
    public static final DefaultRetryDecider INSTANCE = new DefaultRetryDecider();

    private DefaultRetryDecider() {
    }

    @Override
    public boolean shouldRetry(int attempt, NetFailure f, ProviderResponse r) {
        if (r != null) {
            int s = r.httpStatus();
            return s == 408 || s == 429 || (s >= 500 && s <= 504);
        }
        if (f == null) return false;
        return switch (f.type()) {
            case TIMEOUT, DNS, CONNECT, IO -> true;
            default -> false;
        };
    }
}
