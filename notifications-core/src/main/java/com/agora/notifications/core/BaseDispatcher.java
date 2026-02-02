package com.agora.notifications.core;

import com.agora.notifications.api.NotificationMessage;
import com.agora.notifications.api.NotificationReceipt;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.UnknownProviderException;

import java.util.concurrent.CompletionStage;

public abstract class BaseDispatcher<T extends NotificationMessage> implements ChannelDispatcher<T> {
    protected abstract void validate(T message, NotificationContext ctx);

    protected abstract ProviderId resolveProviderId(NotificationContext ctx);

    @Override
    public NotificationReceipt send(T message, NotificationContext ctx) {
        validate(message, ctx);
        ProviderId pid = resolveProviderId(ctx);
        ProviderConfig pcfg = ctx.config().providerConfig(pid);
        if (pcfg == null) throw new UnknownProviderException(pid.value());
        ProviderAdapter<T> adapter = ctx.adapters().get(pid, channel());
        ProviderErrorMapper mapper = ctx.errors().get(pid);
        ProviderRequest req = adapter.toRequest(message, pcfg);
        try {
            ProviderResponse resp = ctx.transport().execute(req).toCompletableFuture().join();
            ProviderOutcome out = mapper.map(resp);
            return out.success() ? NotificationReceipt.sent(message.metadata().correlationId(), channel(), pid)
                    : NotificationReceipt.failed(message.metadata().correlationId(), channel(), pid, out.errorCode(), out.errorMessage());
        } catch (Exception e) {
            NetFailure nf = ctx.exceptionMapper().map(e);
            ProviderOutcome out = mapper.map(nf);
            return NotificationReceipt.failed(message.metadata().correlationId(), channel(), pid, out.errorCode(), out.errorMessage());
        }
    }

    @Override
    public CompletionStage<NotificationReceipt> sendAsync(T message, NotificationContext ctx) {
        return ctx.executor().submit(() -> send(message, ctx));
    }
}
