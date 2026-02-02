package com.agora.notifications.core;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;

public interface AsyncExecutor {
    <T> CompletionStage<T> submit(Callable<T> task);
}