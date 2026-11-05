# ColdTrace AI Assistance Service

Reuses ColdTrace's structured-output provider port and the existing OpenAI and
Ollama adapters. Dashboard interpretation composes evidence through owner APIs.
This service is stateless: it has no MySQL schema and does not persist or approve
incident resolution plans. Alert owns human decisions and their transaction;
Report owns the factual report and its advisory summary workflow.

## Runtime

Java 26, Spring Boot 4.0.6 and Maven Wrapper. Run `./mvnw clean test` and
`./mvnw spring-boot:run` (`.\mvnw.cmd` on Windows). Local port: 8087.

| Variable | Purpose |
| --- | --- |
| `IDENTITY_URL` | Identity, default `http://localhost:8081` |
| `ASSET_URL`, `MONITORING_URL` | Owner evidence, ports 8082 and 8083 |
| `ALERT_URL`, `REPORT_URL` | Owner evidence, ports 8084 and 8085 |
| `MAINTENANCE_URL`, `SUBSCRIPTION_URL` | Owner evidence, ports 8086 and 8088 |
| `INTERNAL_SERVICE_KEY` | Required workload credential, at least 32 characters |
| `AI_ASSISTANCE_ENABLED` | `false` by default |
| `AI_MODEL_PROVIDER` | `ollama` or `openai` when explicitly enabled |
| `AI_MODEL_NAME` | Explicit provider model name |
| `OLLAMA_BASE_URL` | Ollama URL, default `http://localhost:11434` |
| `OPENAI_API_KEY` | Required only for the OpenAI provider |
| `PORT` | Listener port; Docker uses 8080 |

The internal `/internal/ai/v1/generations/` API requires `X-Service-Key`.
It publishes typed advisory content or a controlled failure, never fabricated
fallback advice. A browser JWT cannot call this provider port. The public
organization-scoped dashboard API validates its authenticated actor.

Evidence queries are bounded: up to 1000 recent sensor readings and 100 completed
reports. They are outside the telemetry ingestion path. The model receives the
evidence assembled by its caller; it does not execute operational changes.

Six tests verify owner HTTP contracts, startup without a database, workload
access and disabled-provider behavior. No paid provider is activated by the tests.
OpenAPI: `/v3/api-docs`. Health: `/actuator/health`.
