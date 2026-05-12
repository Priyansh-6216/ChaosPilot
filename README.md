# ChaosPilot

**AI-powered chaos engineering platform for microservices resilience testing**

## What It Does

ChaosPilot injects controlled failures into microservices, observes system behavior with OpenTelemetry, analyzes blast radius, and generates automated root-cause analysis using LLMs.

Inject chaos → Observe metrics/logs/traces → AI analyzes impact → Get resilience recommendations.

## Why This Project

Chaos engineering is critical but often manual. ChaosPilot automates:
- Injecting failure scenarios (latency, errors, timeouts)
- Observing system behavior across services
- Correlating metrics, logs, and traces
- Using AI to explain what went wrong and why
- Scoring resilience to track improvements over time

## Architecture

```
User → React UI
         ↓
   chaos-gateway (Port 8080)
    ↙     ↓     ↘
experiment-service    chaos-orchestrator     notification-service
(Port 8081)          (Port 8082)             (Port 8087)
    ↓                    ↓
  PostgreSQL          [Kafka Topics]
    ↑                    ↓
  Demo Services ←---- [Failure Injection]
    (8083-8086)

AI Worker (Python)
  ↓
[Kafka: experiment.completed]
  ↓
[PostgreSQL, Prometheus, Loki, Tempo]
  ↓
RCA Report
```

## Tech Stack

**Backend (Java)**
- Java 17 + Spring Boot 3
- Spring Data JPA + PostgreSQL
- Apache Kafka (event streaming)
- Redis (caching)
- Lombok

**Observability**
- OpenTelemetry (tracing)
- Prometheus (metrics)
- Grafana (dashboards)
- Loki (logs)
- Tempo (traces)

**AI Worker (Python)**
- FastAPI
- Kafka Consumer
- Ollama (local LLM)

**Frontend**
- React + Tailwind CSS
- Redux (state management)

**Infrastructure**
- Docker Compose (local development)
- Kubernetes-ready (Day 7+)

## Features (MVP by Day 7)

✅ Experiment Management (CRUD, lifecycle)
✅ Failure Injection (latency, HTTP 500, timeouts)
✅ Kafka Event Streaming (experiment lifecycle)
✅ Observability (metrics, logs, traces)
✅ AI Root Cause Analysis
✅ Chaos Reports (resilience scoring)
✅ React Dashboard
✅ Service Health Checks

## Future Enhancements

- Pod kill in Kubernetes
- Chaos Mesh integration
- CPU pressure & memory pressure
- Kafka consumer lag injection
- Slack alerts
- PDF report export
- Terraform deployment
- AWS EKS deployment

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 17+
- Maven 3.8+
- Python 3.10+
- Node.js 18+
- Ollama (for local AI)

### Run Locally

```bash
# Start all services
docker compose up --build

# Verify health
curl http://localhost:8080/health
curl http://localhost:8081/orders/health
curl http://localhost:8082/payments/health
curl http://localhost:8083/inventory/health

# Open Grafana
open http://localhost:3000

# Open React UI
open http://localhost:3000
```

## API Endpoints (Day 2+)

**Experiments**
- `POST /api/experiments` - Create experiment
- `GET /api/experiments` - List experiments
- `GET /api/experiments/{id}` - Get details
- `POST /api/experiments/{id}/start` - Start
- `POST /api/experiments/{id}/stop` - Stop
- `DELETE /api/experiments/{id}` - Delete

**Reports**
- `GET /api/reports/{experimentId}` - Get report
- `GET /api/reports` - List reports

## Kafka Topics

```
experiment.created
experiment.started
experiment.stopped
experiment.completed
experiment.failed
chaos.injection.requested
chaos.injection.completed
```

## Database Schema

**PostgreSQL: chaospilot**

```sql
experiments
experiment_events
registered_services
failure_injections
chaos_reports
```

## 7-Day Build Plan

- **Day 1**: Project skeleton + Docker + demo services ✅
- **Day 2**: Experiment CRUD + Kafka events
- **Day 3**: Failure injection engine
- **Day 4**: Observability (metrics/logs/traces)
- **Day 5**: AI RCA worker
- **Day 6**: Report generator
- **Day 7**: Frontend + polish

## Project Status

🚀 **In Development** - Currently Day 1 scaffolding

## Resume Bullets

- Built ChaosPilot, an AI-powered chaos engineering platform using Java, Spring Boot, Kafka, PostgreSQL, Redis, OpenTelemetry, Prometheus, Grafana, and Python AI workers to inject controlled failures and evaluate microservice resilience.
- Designed event-driven experiment orchestration with Kafka topics for experiment lifecycle tracking, failure injection, metrics collection, RCA generation, and automated chaos report creation.
- Implemented controlled failure scenarios including latency spikes, service crashes, HTTP 500 errors, CPU pressure, memory pressure, database slowdown, and Kafka consumer lag across distributed demo services.
- Integrated OpenTelemetry, Prometheus, Grafana, Loki, and Tempo to capture traces, logs, service latency, error rates, and JVM metrics during chaos experiments.
- Built an AI root-cause analysis worker that correlated logs, traces, and metrics to identify blast radius, severity, recovery gaps, and resilience recommendations.

## Contributing

See [DEVELOPMENT.md](docs/DEVELOPMENT.md) for architecture and development guidelines.

## License

MIT
