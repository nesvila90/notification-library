package com.agora.notifications.core;

import org.slf4j.Logger;

public record NotificationContext(Logger logger, AsyncExecutor executor, NotificationsConfig config,
                                  ProviderAdapterRegistry adapters, ProviderErrorRegistry errors,
                                  Transport transport, HttpExceptionMapper exceptionMapper) {
}
