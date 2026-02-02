# Notification Library – Java Backend Challenge

## 1. Purpose

This project implements a **Java library** that allows sending notifications through **multiple channels** using *
*multiple providers**, providing a **unified interface** for the client code.

The goal of the challenge is to demonstrate **software design, extensibility, and clean architecture**, not to build a
production-ready system.

This solution complies with all requirements described in the challenge document.

---

## 2. Scope of the Library

The library supports the following **mandatory notification channels**:

- **Email**
- **SMS**
- **Push Notification**

Each channel can be configured to use **different providers**, and providers can be swapped **without changing client
code**.

The library:

- Is **framework-agnostic**
- Uses **Java 21**
- Is configured **only through Java code**
- Does **not require real external API calls**
- Uses **mocked/simulated providers** to demonstrate behavior

---

## 3. High-Level Design

### 3.1 Unified Interface

The library exposes a single entry point for sending notifications:

```java
interface Notifications {
    CompletionStage<NotificationResult> send(NotificationMessage message);
}
````

This ensures:

* Client code is decoupled from channels and providers
* New channels/providers do not affect existing clients

---

### 3.2 Notification Channels

Each notification type is modeled explicitly:

* `EmailNotification`
* `SmsNotification`
* `PushNotification`

All notification types share a common base abstraction:

```java
interface NotificationMessage {
    NotificationChannel channel();
}
```

---

### 3.3 Providers

Each channel can have **multiple providers**.

Examples:

* Email: SendGrid, Mailgun
* SMS: Twilio, Nexmo
* Push: FCM, APNS, WebPush

Providers implement a common contract and are selected at runtime via configuration.

---

## 4. Configuration (Code-Based Only)

As required by the challenge, **no YAML, properties or external configuration files** are used.

All configuration is done via Java classes.

Example:

```java
NotificationsConfig config = NotificationsConfig.builder()
        .enableChannel(NotificationChannel.EMAIL, ProviderId.of("sendgrid"))
        .enableChannel(NotificationChannel.SMS, ProviderId.of("twilio"))
        .enableChannel(NotificationChannel.PUSH, ProviderId.of("fcm"))
        .build();
```

Provider-specific configuration is also provided via Java objects:

```java
SendGridConfig sendGridConfig = new SendGridConfig("api-key");
TwilioConfig twilioConfig = new TwilioConfig("accountSid", "token");
```

---

## 5. Provider Integration Strategy

### 5.1 Adapter Pattern

Each provider is implemented as an **adapter** that translates a canonical notification into a provider-specific
request.

```java
interface NotificationProvider<T extends NotificationMessage> {
    ProviderResult send(T message);
}
```

This allows:

* Adding new providers without modifying existing code
* Keeping provider-specific logic isolated

---

### 5.2 Push Notifications (Special Focus)

Push notifications are handled separately because they differ significantly between providers.

The library models push providers explicitly:

* Firebase Cloud Messaging (FCM)
* Apple Push Notification Service (APNS)
* Web Push

Even though calls are simulated, each adapter follows the **official API structure**:

* Headers
* Payload shape
* Required identifiers (tokens, topics, etc.)

---

## 6. Execution Model

### 6.1 Asynchronous Execution

Notification sending is **asynchronous**, returning `CompletionStage`.

* Fire-and-forget behavior
* No blocking calls in the client
* Suitable for batch processing

---

### 6.2 Batch Support

The library supports sending multiple notifications in batch:

```java
CompletionStage<List<NotificationResult>> sendBatch(List<NotificationMessage> messages);
```

---

## 7. Error Handling

The library distinguishes between:

### 7.1 Validation Errors

* Invalid email address
* Invalid phone number
* Missing required fields

These errors are detected **before sending**.

### 7.2 Delivery Errors

* Provider failures
* Network errors (simulated)

Errors are returned in a structured `NotificationResult` object.

---

## 8. Retry Strategy

A simple retry mechanism is implemented for **transient errors**.

* Retries apply only to provider delivery failures
* Validation errors are not retried
* Retry policy is configurable via code

---

## 9. Testing Strategy

As required, the project includes **unit tests** using mocks and simulations.

* No real HTTP calls
* Providers use mock transports
* Deterministic and fast tests

Tests can be executed with:

```bash
mvn test
```

---

## 10. Mocked Providers

To comply with the challenge constraints:

* All provider integrations are **simulated**
* No real credentials are required
* Provider adapters still model real-world request/response behavior

This demonstrates understanding of provider APIs without external dependencies.

---

## 11. Project Structure

```
notifications-parent
├── notifications-api
├── notifications-core
├── notifications-channel-email
├── notifications-channel-sms
├── notifications-channel-push
├── notifications-provider-sendgrid
├── notifications-provider-twilio
├── notifications-provider-fcm
├── notifications-provider-apns
├── notifications-provider-webpush
└── notifications-examples
```

---

## 12. Running the Example

### 12.1. Configuration (Java Only)

``` java
NotificationsConfig config = NotificationsConfig.builder()
.enable(NotificationChannel.EMAIL, ProviderId.of("sendgrid"))
.enable(NotificationChannel.SMS, ProviderId.of("twilio"))
.enable(NotificationChannel.PUSH, ProviderId.of("fcm"))
.providerConfig(SendGridConfig.demo())
.providerConfig(TwilioConfig.demo())
.providerConfig(FcmConfig.demo())
.build();
```

## 12.2. Example Implementation (REAL CODE)

This is the exact way the library is used in notifications-examples.

``` java
public final class DemoMain {

    public static void main(String[] args) {

        Transport transport =
            TransportPipeline.build(
                new MockTransport()
            );
    
        NotificationsConfig config =
            NotificationsConfig.builder()
                .enable(NotificationChannel.EMAIL, ProviderId.of("sendgrid"))
                .enable(NotificationChannel.SMS, ProviderId.of("twilio"))
                .enable(NotificationChannel.PUSH, ProviderId.of("fcm"))
                .providerConfig(SendGridConfig.demo())
                .providerConfig(TwilioConfig.demo())
                .providerConfig(FcmConfig.demo())
                .build();
    
        ProviderAdapters adapters =
            new ProviderAdapters()
                .register(new SendGridEmailAdapter())
                .register(new TwilioSmsAdapter())
                .register(new FcmPushAdapter())
                .register(new ApnsPushAdapter())
                .register(new WebPushAdapter());
    
        Notifications notifications =
            new NotificationEngine(
                config,
                adapters,
                transport,
                Map.of(
                    NotificationChannel.EMAIL, new EmailDispatcher(),
                    NotificationChannel.SMS, new SmsDispatcher(),
                    NotificationChannel.PUSH, new PushDispatcher()
                )
            );
    
        NotificationMessage email =
            EmailBuilder.create()
                .to("user@test.com")
                .subject("Test Email")
                .body("Email body")
                .build();
    
        NotificationMessage sms =
            SmsBuilder.create()
                .phoneE164("+573118486266")
                .body("SMS body")
                .build();
    
        NotificationMessage push =
            PushBuilder.create()
                .deviceToken("device-token")
                .title("Push title")
                .body("Push body")
                .build();
    
        notifications.sendAsync(email);
        notifications.sendAsync(sms);
        notifications.sendAsync(push);
    }
}
```

A simple demo is included to demonstrate usage.

```bash
mvn -DskipTests install
mvn -pl notifications-examples exec:java
```

The example sends:

* One email
* One SMS
* One push notification

---

## 13. Docker (Optional)

A Dockerfile is provided to run the example without local Java installation.

```bash
docker build -t notifications-challenge .
docker run --rm notifications-challenge
```

---

## 14. Security Considerations

* API keys and credentials are not hardcoded
* Provider configs are injected programmatically
* In real usage, secrets should come from environment variables or secret managers

---

## 15. Limitations and Trade-offs

* No real provider integrations (by design)
* No persistence layer
* No delivery callbacks

These were intentionally omitted to focus on architecture and design, as requested.

---

## 16. Use of AI Tools

AI assistance was used to:

* Review design consistency
* Refactor code structure
* Improve documentation clarity

All architectural decisions and final implementation choices were made manually.

