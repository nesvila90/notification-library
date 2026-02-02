package com.agora.notifications.channel.email;

import com.agora.notifications.api.ChannelDisabledException;
import com.agora.notifications.api.EmailMessage;
import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.ValidationException;
import com.agora.notifications.core.BaseDispatcher;
import com.agora.notifications.core.NotificationContext;
import com.agora.notifications.core.Validation;

public final class EmailDispatcher extends BaseDispatcher<EmailMessage> {
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    protected void validate(EmailMessage m, NotificationContext ctx) {
        var p = ctx.config().policy(NotificationChannel.EMAIL);
        if (p == null || !p.enabled()) throw new ChannelDisabledException(NotificationChannel.EMAIL);
        if (!Validation.isEmail(m.toEmail())) throw new ValidationException("Invalid email: " + m.toEmail());
        if (m.subject() == null || m.subject().isBlank()) throw new ValidationException("Email subject required");
        if (m.body() == null || m.body().isBlank()) throw new ValidationException("Email body required");
    }

    @Override
    protected ProviderId resolveProviderId(NotificationContext ctx) {
        return ctx.config().policy(NotificationChannel.EMAIL).activeProvider();
    }
}
