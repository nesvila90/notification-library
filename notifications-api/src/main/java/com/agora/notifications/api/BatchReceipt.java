package com.agora.notifications.api;

import java.util.List;

public record BatchReceipt(List<NotificationReceipt> receipts) {
    public BatchReceipt {
        receipts = List.copyOf(receipts);
    }
}
