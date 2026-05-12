# Quick Reference

## Service Ports

| Service | Port | Endpoint |
|---------|------|----------|
| API Gateway | 8080 | http://localhost:8080/health |
| Experiment Service | 8081 | http://localhost:8081/health |
| Chaos Orchestrator | 8082 | http://localhost:8082/health |
| Order Service | 8083 | http://localhost:8083/health |
| Payment Service | 8084 | http://localhost:8084/health |
| Inventory Service | 8085 | http://localhost:8085/health |
| User Service | 8086 | http://localhost:8086/health |
| Notification Service | 8087 | http://localhost:8087/health |
| Metrics Collector | 8089 | http://localhost:8089/health |
| Trace Analyzer | 8090 | http://localhost:8090/health |
| Report Service | 8091 | http://localhost:8091/health |

## Infrastructure Ports

| Service | Port | URL |
|---------|------|-----|
| PostgreSQL | 5432 | postgres://chaospilot:chaospilot123@localhost:5432/chaospilot |
| Redis | 6379 | redis://localhost:6379 |
| Kafka | 9092 | localhost:9092 |
| Zookeeper | 2181 | localhost:2181 |
| Prometheus | 9090 | http://localhost:9090 |
| Grafana | 3000 | http://localhost:3000 (admin/admin) |
| Loki | 3100 | http://localhost:3100 |
| Tempo | 3200 | http://localhost:3200 |
| OTEL Collector | 4317 | grpc://localhost:4317 |

## Quick Commands

### Docker Compose

```bash
# Start all services
cd infra && docker compose up -d

# View logs
docker compose logs -f

# Logs for specific service
docker compose logs -f experiment-service

# Stop all
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v

# Rebuild and restart specific service
docker compose up -d --build experiment-service
```

### Maven

```bash
# Build all
mvn clean package -DskipTests

# Run tests
mvn test

# Build single service
mvn -pl backend/chaos-gateway clean package

# Run service locally
mvn -pl backend/experiment-service spring-boot:run
```

### Database

```bash
# Connect to PostgreSQL
docker exec -it chaospilot-postgres psql -U chaospilot -d chaospilot

# List tables
\dt

# Query experiments
SELECT * FROM experiments;

# Exit
\q
```

### Kafka

```bash
# List topics
docker exec chaospilot-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Create topic
docker exec chaospilot-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic test-topic

# Consume messages
docker exec chaospilot-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic experiment.created --from-beginning
```

### Testing APIs

```bash
# Check all health endpoints
for port in 8080 8081 8082 8083 8084 8085 8086 8087; do
  echo "Port $port:"
  curl -s http://localhost:$port/health && echo ""
done

# Test Prometheus metrics
curl http://localhost:8081/actuator/prometheus | head -20

# Test API Gateway
curl http://localhost:8080/health
```

## Project Files Overview

### Root Level
- `pom.xml` - Maven parent project
- `README.md` - Project documentation
- `.gitignore` - Git ignore rules

### Documentation
- `docs/GETTING_STARTED.md` - Setup and quick start
- `docs/architecture.md` - System design
- `docs/DEVELOPMENT.md` - Development guide
- `docs/api-contracts.md` - API specifications

### Infrastructure
- `infra/docker-compose.yml` - All services orchestration
- `infra/db-init.sql` - PostgreSQL schema
- `infra/prometheus/prometheus.yml` - Metrics scraping config
- `infra/otel/` - OpenTelemetry configurations

### Backend Services
Each service has:
- `pom.xml` - Service dependencies
- `Dockerfile` - Multi-stage build
- `src/main/resources/application.yml` - Configuration
- `src/main/java/com/chaospilot/[service]/` - Source code
- `src/test/` - Tests (scaffold ready)

### Demo Services
Same structure as backend services with chaos injection capability.

## Next Steps (Day 2)

1. Implement Experiment CRUD endpoints
2. Create Experiment entity with @Entity, @Repository
3. Create ExperimentService with business logic
4. Create ExperimentController with REST APIs
5. Publish Kafka events on experiment state changes
6. Test APIs with curl/Postman
7. Verify Kafka messages in Kafka UI

## Key Files by Priority

**Must modify for Day 2:**
1. `backend/experiment-service/src/main/java/.../entity/Experiment.java`
2. `backend/experiment-service/src/main/java/.../repository/ExperimentRepository.java`
3. `backend/experiment-service/src/main/java/.../service/ExperimentService.java`
4. `backend/experiment-service/src/main/java/.../controller/ExperimentController.java`
5. `backend/experiment-service/src/main/java/.../dto/CreateExperimentRequest.java`
6. `backend/experiment-service/src/main/java/.../kafka/ExperimentEventPublisher.java`

## Troubleshooting

**Issue: "Port already in use"**
```bash
# Find and kill process using port
lsof -i :8080
kill -9 <PID>
```

**Issue: Services not connecting to database**
```bash
# Wait for database to be ready
docker exec chaospilot-postgres pg_isready -U chaospilot

# Check logs
docker compose logs postgres
```

**Issue: Kafka topics not created**
- Topics auto-create when first message is published
- Or manually create: `docker exec chaospilot-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic test-topic`

**Issue: Out of memory**
- Increase Docker memory: Docker Desktop → Preferences → Resources → Memory (8GB+)

## Useful Aliases

```bash
# Add to ~/.zshrc or ~/.bashrc
alias cp-start='cd ~/Desktop/ChaosPilot/infra && docker compose up -d'
alias cp-logs='cd ~/Desktop/ChaosPilot/infra && docker compose logs -f'
alias cp-stop='cd ~/Desktop/ChaosPilot/infra && docker compose down'
alias cp-build='cd ~/Desktop/ChaosPilot && mvn clean package -DskipTests'
alias cp-test='cd ~/Desktop/ChaosPilot && mvn test'
alias cp-shell='docker exec -it chaospilot-postgres psql -U chaospilot -d chaospilot'
```

## Performance Tips

- Run `docker compose up -d` once, then `docker compose logs -f` to watch
- Use `--build` only when Dockerfile changes
- Build locally with Maven instead of Docker for faster iteration
- Use `-DskipTests` for quick builds
- Set `MAVEN_OPTS="-Xmx2g"` to allocate more memory to Maven
