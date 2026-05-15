# ChaosPilot Day 4 Summary

## Day 4 - Observability and Monitoring

This day completes the observability stack for ChaosPilot, enabling metrics, logs, and tracing across all services.

### What’s implemented

- Prometheus scraping is configured in `infra/prometheus/prometheus.yml`.
- Grafana is provisioned in Docker Compose as `chaospilot-grafana`.
- Loki is configured for log aggregation in `infra/otel/loki-config.yml`.
- Tempo is configured for trace storage in `infra/otel/tempo-config.yml`.
- OpenTelemetry Collector is configured in `infra/otel/otel-collector-config.yml`.
- All backend and demo services expose Spring Boot actuator metrics and OTLP tracing endpoints.

### Instrumentation

Each service includes observability configuration for:
- `/actuator/health`
- `/actuator/prometheus`
- OpenTelemetry exporter to `http://otel-collector:4317`
- Prometheus metrics scraping via `management.endpoints.web.exposure.include`

### Key files

- `infra/prometheus/prometheus.yml`
- `infra/docker-compose.yml`
- `infra/otel/otel-collector-config.yml`
- `infra/otel/loki-config.yml`
- `infra/otel/tempo-config.yml`
- `backend/experiment-service/src/main/resources/application.yml`
- `demo-services/*.*/src/main/resources/application.yml`

### How to verify

1. Start the stack:
   ```bash
   cd infra
   docker compose up --build
   ```
2. Open Prometheus: `http://localhost:9090`
3. Open Grafana: `http://localhost:3000` (admin/admin)
4. Open Tempo: `http://localhost:3200`
5. Verify metrics and traces from services in Grafana Explore.
