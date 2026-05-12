# Development Guide

## Project Structure

```
chaospilot/
├── backend/                    # Core platform services (Java/Spring Boot 3)
│   ├── chaos-gateway/         # API Gateway (port 8080)
│   ├── chaos-orchestrator/    # Orchestrates experiments (port 8082)
│   ├── experiment-service/    # Experiment CRUD APIs (port 8081)
│   ├── metrics-collector/     # Collects metrics (port 8089)
│   ├── trace-analyzer/        # Analyzes traces (port 8090)
│   ├── report-service/        # Generates reports (port 8091)
│   └── notification-service/  # Sends notifications (port 8087)
│
├── demo-services/             # Demo microservices
│   ├── order-service/         # Order orchestration (port 8083)
│   ├── payment-service/       # Payment processing (port 8084)
│   ├── inventory-service/     # Inventory management (port 8085)
│   └── user-service/          # User management (port 8086)
│
├── ai-worker/                 # Python AI services
│   └── rca-worker/           # Root cause analysis (Day 5)
│
├── frontend/                  # React UI
│   └── chaos-ui/             # Dashboard (Day 7)
│
├── infra/                     # Infrastructure
│   ├── docker-compose.yml    # All services orchestration
│   ├── db-init.sql           # Database schema
│   ├── prometheus/           # Prometheus config
│   ├── grafana/              # Grafana configs
│   └── otel/                 # OpenTelemetry configs
│
├── docs/                      # Documentation
├── pom.xml                    # Maven parent
└── README.md
```

## Technology Stack

**Backend**
- Java 17
- Spring Boot 3.2
- Spring Cloud (Gateway, OpenFeign)
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- Redis
- OpenTelemetry
- Micrometer Prometheus
- Lombok

**Observability**
- Prometheus (metrics)
- Grafana (dashboards)
- Loki (logs)
- Tempo (traces)
- OpenTelemetry Collector (OTEL)

**Infrastructure**
- Docker & Docker Compose
- PostgreSQL 15
- Apache Kafka
- Zookeeper
- Redis

**AI/ML (Day 5)**
- Python 3.10+
- FastAPI
- Ollama (local LLM)
- Kafka Consumer

**Frontend (Day 7)**
- React 18
- TypeScript
- Tailwind CSS
- Redux (state management)

## Development Setup

### 1. Install Dependencies

```bash
# macOS
brew install java17 maven docker

# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk maven docker.io

# Windows
# Download from https://adoptium.net/ and https://maven.apache.org/
```

### 2. Project Build

```bash
# Build entire project
mvn clean package

# Build single service
cd backend/chaos-gateway
mvn clean package

# Build with skip tests
mvn clean package -DskipTests

# Run tests
mvn test
```

### 3. Running Services Locally

#### Option A: Docker Compose (Recommended for Day 1)

```bash
cd infra
docker compose up --build
```

#### Option B: Local Development

```bash
# Terminal 1: PostgreSQL
docker run -d --name postgres-local \
  -e POSTGRES_DB=chaospilot \
  -e POSTGRES_USER=chaospilot \
  -e POSTGRES_PASSWORD=chaospilot123 \
  -p 5432:5432 \
  postgres:15

# Terminal 2: Kafka
docker run -d --name kafka-local \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -p 9092:9092 \
  confluentinc/cp-kafka:7.5.0

# Terminal 3: Run experiment-service
cd backend/experiment-service
mvn spring-boot:run

# Terminal 4: Run order-service
cd demo-services/order-service
mvn spring-boot:run
```

## Code Organization

### Service Structure

Each backend service follows this pattern:

```
service-name/
├── pom.xml                     # Maven configuration
├── Dockerfile                  # Docker build
├── src/main/
│   ├── java/com/chaospilot/[service]/
│   │   ├── [Service]Application.java          # Entry point
│   │   ├── controller/                        # REST endpoints
│   │   ├── service/                           # Business logic
│   │   ├── repository/                        # Data access (JPA)
│   │   ├── entity/                            # JPA entities
│   │   ├── dto/                               # Data transfer objects
│   │   ├── kafka/                             # Kafka producers/consumers
│   │   ├── config/                            # Spring configurations
│   │   └── exception/                         # Custom exceptions
│   └── resources/
│       └── application.yml                    # Spring Boot config
└── src/test/
    └── java/                                  # Unit & integration tests
```

### Example: Add New Experiment API

**1. Create Entity** (`backend/experiment-service/src/main/java/.../entity/Experiment.java`)
```java
@Entity
@Table(name = "experiments")
@Data
@NoArgsConstructor
public class Experiment {
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String targetService;
    
    @Enumerated(EnumType.STRING)
    private FailureType failureType;
    
    @Enumerated(EnumType.STRING)
    private ExperimentStatus status = ExperimentStatus.CREATED;
    
    @Column(nullable = false)
    private Integer durationSeconds;
    
    private Integer intensity;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
```

**2. Create Repository** (`backend/experiment-service/src/main/java/.../repository/ExperimentRepository.java`)
```java
@Repository
public interface ExperimentRepository extends JpaRepository<Experiment, UUID> {
    List<Experiment> findByStatus(ExperimentStatus status);
    List<Experiment> findByTargetService(String targetService);
}
```

**3. Create Service** (`backend/experiment-service/src/main/java/.../service/ExperimentService.java`)
```java
@Service
@Slf4j
public class ExperimentService {
    private final ExperimentRepository repository;
    private final KafkaTemplate kafkaTemplate;
    
    public Experiment createExperiment(CreateExperimentRequest request) {
        Experiment experiment = new Experiment();
        // Set fields...
        repository.save(experiment);
        
        // Publish event
        kafkaTemplate.send("experiment.created", experiment);
        return experiment;
    }
}
```

**4. Create DTO** (`backend/experiment-service/src/main/java/.../dto/CreateExperimentRequest.java`)
```java
@Data
public class CreateExperimentRequest {
    @NotBlank
    private String name;
    
    @NotBlank
    private String targetService;
    
    @NotNull
    private FailureType failureType;
    
    @Min(1)
    private Integer durationSeconds;
    
    @Min(1)
    @Max(100)
    private Integer intensity;
}
```

**5. Create Controller** (`backend/experiment-service/src/main/java/.../controller/ExperimentController.java`)
```java
@RestController
@RequestMapping("/api/experiments")
@Slf4j
public class ExperimentController {
    private final ExperimentService service;
    
    @PostMapping
    public ResponseEntity<Experiment> createExperiment(@Valid @RequestBody CreateExperimentRequest request) {
        return ResponseEntity.status(201).body(service.createExperiment(request));
    }
    
    @GetMapping
    public ResponseEntity<List<Experiment>> listExperiments() {
        return ResponseEntity.ok(service.listExperiments());
    }
}
```

## Testing

### Unit Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ExperimentServiceTest

# Run with coverage
mvn test jacoco:report
```

### Integration Tests

```bash
# Run tests that connect to database
mvn test -Dgroups=integration

# With Docker services running
docker compose up -d
mvn test
docker compose down
```

### Manual Testing

#### Using curl

```bash
# Create experiment
curl -X POST http://localhost:8081/api/experiments \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test latency",
    "targetService": "payment-service",
    "failureType": "LATENCY",
    "durationSeconds": 120,
    "intensity": 70
  }'

# List experiments
curl http://localhost:8081/api/experiments

# Start experiment
curl -X POST http://localhost:8081/api/experiments/{experimentId}/start

# Stop experiment
curl -X POST http://localhost:8081/api/experiments/{experimentId}/stop
```

#### Using Postman/Insomnia

- Import: `docs/ChaosPilot.postman_collection.json` (create in Day 2)
- Base URL: `http://localhost:8080`

## Debugging

### Enable Debug Logging

In `application.yml`:
```yaml
logging:
  level:
    com.chaospilot: DEBUG
    org.springframework: DEBUG
```

### Remote Debugging

```bash
# Start service with debug port
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar

# Connect IDE debugger to localhost:5005
```

### View Metrics in Prometheus

Navigate to: http://localhost:9090

Search for:
- `http_server_requests_seconds` - Request latency
- `jvm_memory_used_bytes` - Memory usage
- `process_cpu_usage` - CPU usage

### View Traces in Grafana

1. http://localhost:3000 → Explore
2. Data Source: Tempo
3. Search for service name

## Common Commands

```bash
# Build all
mvn clean package

# Build and skip tests
mvn clean package -DskipTests

# Run single service
mvn -pl backend/chaos-gateway spring-boot:run

# Format code
mvn spotless:apply

# Check dependencies
mvn dependency:tree

# Update dependencies
mvn versions:display-dependency-updates

# Show Maven effective pom
mvn help:effective-pom
```

## Best Practices

1. **Environment Variables** - Use for configuration, not hardcoded values
2. **Logging** - Use structured JSON logging
3. **Error Handling** - Create custom exceptions
4. **Testing** - Aim for 80%+ code coverage
5. **Documentation** - Add JavaDoc to public methods
6. **Code Style** - Follow Google Java Style Guide
7. **Dependencies** - Keep updated but test thoroughly
8. **Database Migrations** - Use Liquibase or Flyway (future)

## CI/CD Setup (Future)

- GitHub Actions for automated tests
- Build on PR, deploy on merge to main
- SonarQube for code quality
- Docker Hub for image registry
