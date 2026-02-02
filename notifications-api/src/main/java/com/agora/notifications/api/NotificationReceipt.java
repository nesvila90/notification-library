package com.agora.notifications.api;

public record NotificationReceipt(String correlationId, NotificationChannel channel, ProviderId providerId,
                                  NotificationStatus status, String errorCode, String errorMessage) {
    public static NotificationReceipt sent(String cid, NotificationChannel ch, ProviderId pid) {
        return new NotificationReceipt(cid, ch, pid, NotificationStatus.SENT, null, null);
    }

    public static NotificationReceipt failed(String cid, NotificationChannel ch, ProviderId pid, String code, String msg) {
        return new NotificationReceipt(cid, ch, pid, NotificationStatus.FAILED, code, msg);
    }
}
