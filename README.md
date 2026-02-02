# Notifications Challenge - Pro Refactor (Java 21, Maven Multi-Module)

## What changed (Pro Refactor)

- Providers are **Adapters** + **ErrorMappers**:
    - Adapter: canonical message -> ProviderRequest (official-like HTTP shape)
    - ErrorMapper: ProviderResponse/NetFailure -> ProviderOutcome (retry hint + normalized codes)
- Providers are discovered via **ServiceLoader** (no core changes to add new providers)
- HTTP execution is a **Transport pipeline** (decorators):
    - Retry (exp backoff + jitter)
    - Circuit Breaker
    - Bulkhead
    - Observability
- Leaf transport can be `JavaHttpClientTransport` or `MockTransport`

## Run tests

```bash
mvn -q test
```

## Run demo

```bash
mvn -q -pl notifications-examples exec:java -Dexec.mainClass=com.agora.notifications.examples.DemoMain
```
