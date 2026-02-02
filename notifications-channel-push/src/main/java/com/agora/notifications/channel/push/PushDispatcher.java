package com.agora.notifications.channel.push;

import com.agora.notifications.api.ChannelDisabledException;
import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.PushMessage;
import com.agora.notifications.api.ValidationException;
import com.agora.notifications.core.BaseDispatcher;
import com.agora.notifications.core.NotificationContext;

public final class PushDispatcher extends BaseDispatcher<PushMessage> {
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.PUSH;
    }

    @Override
    protected void validate(PushMessage m, NotificationContext ctx) {
        var p = ctx.config().policy(NotificationChannel.PUSH);
        if (p == null || !p.enabled()) throw new ChannelDisabledException(NotificationChannel.PUSH);
        if (m.target() == null) throw new ValidationException("Push target required");
        if (m.title() == null || m.title().isBlank()) throw new ValidationException("Push title required");
        if (m.body() == null || m.body().isBlank()) throw new ValidationException("Push body required");
    }

    @Override
    protected ProviderId resolveProviderId(NotificationContext ctx) {
        return ctx.config().policy(NotificationChannel.PUSH).activeProvider();
    }
}
