package com.agora.notifications.core;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DefaultAsyncExecutor implements AsyncExecutor, AutoCloseable {
    private final ExecutorService pool;

    public DefaultAsyncExecutor(ExecutorService pool) {
        this.pool = pool;
    }

    public static DefaultAsyncExecutor fixed(int threads) {
        return new DefaultAsyncExecutor(Executors.newFixedThreadPool(threads));
    }

    @Override
    public <T> CompletionStage<T> submit(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, pool);
    }

    @Override
    public void close() {
        pool.shutdown();
    }
}
