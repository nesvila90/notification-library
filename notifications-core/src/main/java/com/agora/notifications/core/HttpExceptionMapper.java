package com.agora.notifications.core;

public interface HttpExceptionMapper {
    NetFailure map(Throwable t);
}