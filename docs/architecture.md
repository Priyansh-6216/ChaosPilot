# ChaosPilot Architecture

## System Overview

ChaosPilot is a microservices-based chaos engineering platform built with Spring Boot 3, Kafka, and advanced observability tools. It injects controlled failures into services, observes behavior, and uses AI to analyze root causes.

## Core Components

### API Gateway (Port 8080)
- Spring Cloud Gateway
- Routes requests to all microservices
- Centralized entry point

### Experiment Service (Port 8081)
- CRUD operations for chaos experiments
- Manages experiment lifecycle (CREATED → RUNNING → STOPPED → COMPLETED)
- Publishes events to Kafka

### Chaos Orchestrator (Port 8082)
- Listens to experiment events
- Triggers failure injection on target services
- Manages auto-reset after experiment duration

### Demo Services (Ports 8083-8086)
- **Order Service** (8083): Orchestrates order flow
- **Payment Service** (8084): Processes payments
- **Inventory Service** (8085): Manages inventory
- **User Service** (8086): User management

Each demo service has:
- `/internal/chaos/*` endpoints for injecting chaos
- In-memory ChaosState for controlling failures
- OpenTelemetry instrumentation

### Observability Stack

#### Prometheus (Port 9090)
- Scrapes metrics from all services via `/actuator/prometheus`
- Stores timeseries data
- 15-second scrape interval

#### Grafana (Port 3000)
- Visualizes metrics from Prometheus
- Creates dashboards for service health
- Credentials: admin/admin

#### Loki (Port 3100)
- Centralized log aggregation
- Stores logs for trace correlation
- Integrates with Grafana

#### Tempo (Port 3200)
- Distributed tracing backend
- Stores spans and traces
- Integrates with OpenTelemetry Collector

#### OpenTelemetry Collector (Port 4317/4318)
- Ingests traces and metrics from services
- Forwards to Prometheus, Loki, and Tempo
- gRPC and HTTP protocols

### Infrastructure Services

#### PostgreSQL (Port 5432)
- Primary data store
- Database: `chaospilot`
- Tables: experiments, experiment_events, registered_services, failure_injections, chaos_reports

#### Kafka (Port 9092)
- Event streaming backbone
- Topics:
  - `experiment.created`
  - `experiment.started`
  - `experiment.stopped`
  - `experiment.completed`
  - `chaos.injection.requested`
  - `chaos.injection.completed`

#### Redis (Port 6379)
- Caching layer
- Session storage
- Rate limiting

## Data Flow

### Creating & Starting an Experiment

```
1. User creates experiment (POST /api/experiments)
   ↓
2. Experiment Service saves to PostgreSQL
   ↓
3. Publishes experiment.created to Kafka
   ↓
4. User starts experiment (POST /api/experiments/{id}/start)
   ↓
5. Experiment Service updates status to RUNNING
   ↓
6. Publishes experiment.started
   ↓
7. Publishes chaos.injection.requested
   ↓
8. Chaos Orchestrator receives message
   ↓
9. Calls target service /internal/chaos endpoint
   ↓
10. Failure is activated in target service
   ↓
11. Orchestrator publishes chaos.injection.completed
```

### Observing Failures

```
During experiment:
1. Services record metrics (latency, errors, CPU)
2. OpenTelemetry sends traces to Collector
3. Collector exports to Prometheus, Loki, Tempo
4. Metrics appear in Grafana
5. Traces visible in Tempo
6. Logs searchable in Loki
```

### Stopping Experiment

```
1. User stops experiment (POST /api/experiments/{id}/stop)
   ↓
2. Experiment status → STOPPED
   ↓
3. Chaos Orchestrator calls /internal/chaos/reset
   ↓
4. Target service deactivates failures
   ↓
5. Publishes chaos.injection.completed
   ↓
6. Experiment status → COMPLETED
```

## Service Communication

### Synchronous (HTTP/REST)
- API Gateway → Backend Services
- Order Service → Payment Service (client calls)
- Order Service → Inventory Service (client calls)
- Chaos Orchestrator → Demo Services (chaos injection)

### Asynchronous (Kafka)
- Experiment Service → Event Topics
- Orchestrator → Receives from event topics
- Notification Service → Consumes all experiment events

## Kubernetes-Ready Architecture

All services designed for K8s deployment:
- Environment variable configuration
- Health check endpoints
- Graceful shutdown
- Distributed tracing
- Structured logging
- Resource requests/limits ready

## Day 1 Deliverables

✅ All 11 backend services scaffolded
✅ Docker Compose with all infrastructure
✅ PostgreSQL schema with tables
✅ Kafka topics auto-creation enabled
✅ OpenTelemetry integration configured
✅ Health check endpoints on all services
✅ Application properties for all services
✅ Maven multi-module project structure

## Next Steps (Day 2+)

- Day 2: Implement Experiment CRUD APIs
- Day 3: Implement failure injection endpoints
- Day 4: Add observability dashboards
- Day 5: Build AI RCA worker
- Day 6: Generate reports
- Day 7: Build React UI
