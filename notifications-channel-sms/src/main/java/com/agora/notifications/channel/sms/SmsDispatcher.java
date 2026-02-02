package com.agora.notifications.channel.sms;

import com.agora.notifications.api.ChannelDisabledException;
import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.api.SmsMessage;
import com.agora.notifications.api.ValidationException;
import com.agora.notifications.core.BaseDispatcher;
import com.agora.notifications.core.NotificationContext;
import com.agora.notifications.core.Validation;

public final class SmsDispatcher extends BaseDispatcher<SmsMessage> {
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }

    @Override
    protected void validate(SmsMessage m, NotificationContext ctx) {
        var p = ctx.config().policy(NotificationChannel.SMS);
        if (p == null || !p.enabled()) throw new ChannelDisabledException(NotificationChannel.SMS);
        if (!Validation.isE164(m.phoneE164())) throw new ValidationException("Invalid E.164: " + m.phoneE164());
        if (m.body() == null || m.body().isBlank()) throw new ValidationException("SMS body required");
    }

    @Override
    protected ProviderId resolveProviderId(NotificationContext ctx) {
        return ctx.config().policy(NotificationChannel.SMS).activeProvider();
    }
}
