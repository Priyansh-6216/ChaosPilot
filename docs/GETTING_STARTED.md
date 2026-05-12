# Getting Started - Day 1

## Prerequisites

- Docker & Docker Compose (v2.0+)
- Java 17 (for local development)
- Maven 3.8+ (for local development)
- Git

## Quick Start

### 1. Clone the Repository

```bash
cd /path/to/ChaosPilot
```

### 2. Start All Services

```bash
cd infra
docker compose up --build
```

The first run will take 5-10 minutes to build all images.

**Expected output:**
```
chaospilot-postgres is healthy
chaospilot-zookeeper is healthy
chaospilot-kafka is healthy
chaospilot-prometheus is healthy
chaospilot-grafana is healthy
chaospilot-loki is healthy
chaospilot-tempo is healthy
chaospilot-otel-collector is healthy
chaospilot-gateway is healthy
chaospilot-orchestrator is healthy
chaospilot-experiment is healthy
...
```

### 3. Verify Health Checks

```bash
# API Gateway
curl http://localhost:8080/health

# Experiment Service
curl http://localhost:8081/health

# Order Service
curl http://localhost:8083/health

# Payment Service
curl http://localhost:8084/health

# Inventory Service
curl http://localhost:8085/health

# User Service
curl http://localhost:8086/health

# Notification Service
curl http://localhost:8087/health
```

All should return: `OK`

### 4. Access Web UIs

**Grafana (Observability Dashboards)**
- URL: http://localhost:3000
- Username: `admin`
- Password: `admin`

**Prometheus (Metrics)**
- URL: http://localhost:9090

**Prometheus Targets:**
- http://localhost:9090/targets
- Should show all 8 services with status "UP"

### 5. Verify Kafka

```bash
# List topics
docker exec chaospilot-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Expected topics:
# experiment.created
# experiment.started
# experiment.stopped
# experiment.completed
# experiment.failed
# chaos.injection.requested
# chaos.injection.completed
```

### 6. Verify PostgreSQL

```bash
# Connect to PostgreSQL
docker exec -it chaospilot-postgres psql -U chaospilot -d chaospilot

# List tables
\dt

# Exit
\q
```

Tables should include: experiments, experiment_events, registered_services, failure_injections, chaos_reports

## Accessing Logs

### View logs for a specific service

```bash
docker compose logs -f chaos-gateway
docker compose logs -f experiment-service
docker compose logs -f order-service
```

### View all logs

```bash
docker compose logs -f
```

## Development Workflow

### 1. Make Code Changes

Example: Modify `backend/experiment-service/src/main/.../ExperimentServiceApplication.java`

### 2. Rebuild Service

```bash
cd backend/experiment-service
mvn clean package
docker build -t experiment-service:latest .
docker compose up -d experiment-service
```

Or use the compose rebuild:

```bash
docker compose up -d --build experiment-service
```

### 3. View Service Logs

```bash
docker compose logs -f experiment-service
```

## Stopping Services

```bash
# Stop all services (keep volumes)
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v
```

## Common Issues

### Port Already in Use

```bash
# Find process using port (example: 8080)
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Docker Build Failures

```bash
# Clear Maven cache
docker compose down -v
rm -rf ~/.m2/repository/com/chaospilot

# Rebuild
docker compose up --build
```

### Database Connection Errors

Wait for PostgreSQL to be healthy:

```bash
docker compose logs postgres

# Should show "database system is ready"
```

### Out of Memory

Increase Docker memory allocation:

Docker Desktop → Preferences → Resources → Memory

Set to 8GB or more for comfortable development.

## Next Steps

Day 2: Implement Experiment CRUD APIs and test with curl/Postman

Day 3: Implement failure injection endpoints

Day 4: Set up observability dashboards in Grafana

Day 5: Build AI root cause analysis worker

Day 6: Generate chaos reports

Day 7: Build React frontend UI
