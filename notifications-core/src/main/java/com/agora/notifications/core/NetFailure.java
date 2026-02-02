package com.agora.notifications.core;

public record NetFailure(NetErrorType type, Throwable cause) {
}