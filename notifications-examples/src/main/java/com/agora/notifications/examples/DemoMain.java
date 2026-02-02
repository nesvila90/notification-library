package com.agora.notifications.examples;

import com.agora.notifications.api.Builders;
import com.agora.notifications.api.NotificationChannel;
import com.agora.notifications.api.ProviderId;
import com.agora.notifications.channel.email.EmailDispatcher;
import com.agora.notifications.channel.push.PushDispatcher;
import com.agora.notifications.channel.sms.SmsDispatcher;
import com.agora.notifications.core.BulkheadPolicy;
import com.agora.notifications.core.BulkheadTransport;
import com.agora.notifications.core.CircuitBreakerPolicy;
import com.agora.notifications.core.CircuitBreakerTransport;
import com.agora.notifications.core.DefaultAsyncExecutor;
import com.agora.notifications.core.DefaultHttpExceptionMapper;
import com.agora.notifications.core.DefaultRetryDecider;
import com.agora.notifications.core.MockTransport;
import com.agora.notifications.core.NotificationContext;
import com.agora.notifications.core.NotificationEngine;
import com.agora.notifications.core.NotificationsConfig;
import com.agora.notifications.core.ObservabilityTransport;
import com.agora.notifications.core.ProviderAdapterRegistry;
import com.agora.notifications.core.ProviderErrorRegistry;
import com.agora.notifications.core.RetryPolicy;
import com.agora.notifications.core.RetryingTransport;
import com.agora.notifications.core.ServiceLoaderBootstrap;
import com.agora.notifications.core.Transport;
import com.agora.notifications.provider.apns.ApnsConfig;
import com.agora.notifications.provider.fcm.FcmConfig;
import com.agora.notifications.provider.mailgun.MailgunConfig;
import com.agora.notifications.provider.nexmo.NexmoConfig;
import com.agora.notifications.provider.sendgrid.SendGridConfig;
import com.agora.notifications.provider.twilio.TwilioConfig;
import com.agora.notifications.provider.webpush.WebPushConfig;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public final class DemoMain {
    public static void main(String[] args) {
        var log = LoggerFactory.getLogger("demo");

        Transport leaf = new MockTransport(42L, Duration.ofMillis(30), Duration.ofMillis(90));

        Transport transport = new ObservabilityTransport(
                new BulkheadTransport(
                        new CircuitBreakerTransport(
                                new RetryingTransport(
                                        leaf,
                                        new RetryPolicy(3, Duration.ofMillis(100), Duration.ofSeconds(1), 0.2),
                                        DefaultRetryDecider.INSTANCE,
                                        new DefaultHttpExceptionMapper()
                                ),
                                new CircuitBreakerPolicy(3, Duration.ofSeconds(10))
                        ),
                        new BulkheadPolicy(20)
                ),
                log
        );

        try (var exec = DefaultAsyncExecutor.fixed(6)) {
            var cfg = NotificationsConfig.builder()
                    .enable(NotificationChannel.EMAIL, new ProviderId("sendgrid"))
                    .enable(NotificationChannel.SMS, new ProviderId("twilio"))
                    .enable(NotificationChannel.PUSH, new ProviderId("fcm"))
                    .providerConfig(SendGridConfig.builder().apiKey("SG_demo").fromEmail("noreply@example.com").build())
                    .providerConfig(MailgunConfig.builder().apiKey("MG_demo").domain("example.com").build())
                    .providerConfig(TwilioConfig.builder().accountSid("AC123").authToken("tok").fromPhoneE164("+14155550100").build())
                    .providerConfig(NexmoConfig.builder().apiKey("nx").apiSecret("sec").from("Agora").build())
                    .providerConfig(FcmConfig.builder().projectId("my-project").accessToken("ya29.demo").build())
                    .providerConfig(ApnsConfig.builder().topic("com.example.app").authToken("apns.jwt").build())
                    .providerConfig(WebPushConfig.builder().vapidPublicKey("pk").vapidPrivateKey("sk").subject("mailto:dev@example.com").build())
                    .build();

            ProviderAdapterRegistry adapters = ServiceLoaderBootstrap.loadAdapters();
            ProviderErrorRegistry errors = ServiceLoaderBootstrap.loadErrorMappers();

            var ctx = new NotificationContext(
                    log,
                    exec,
                    cfg,
                    adapters,
                    errors,
                    transport,
                    new DefaultHttpExceptionMapper()
            );

            var engine = new NotificationEngine(
                    Map.of(
                            NotificationChannel.EMAIL, new EmailDispatcher(),
                            NotificationChannel.SMS, new SmsDispatcher(),
                            NotificationChannel.PUSH, new PushDispatcher()
                    ),
                    ctx
            );

            var email = Builders.email().to("user@example.com").subject("Hello").body("Test").build();
            var sms = Builders.sms().phoneE164("+573118486266").body("Hola").build();
            var push = Builders.push().deviceToken("devToken").title("Hola").body("Push body").build();

            engine.sendAsync(email).thenAccept(r -> log.info("email receipt={}", r));
            engine.sendAsync(sms).thenAccept(r -> log.info("sms receipt={}", r));
            engine.sendAsync(push).thenAccept(r -> log.info("push receipt={}", r));

            engine.sendBatchAsync(List.of(email, sms, push))
                    .thenAccept(br -> log.info("batch receipts={}", br.receipts()))
                    .toCompletableFuture().join();
        }
    }
}
