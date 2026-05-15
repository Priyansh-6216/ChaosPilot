# ChaosPilot Day 3 Summary

## Day 3 - Failure Injection Engine

This day implements the chaos execution pipeline across the orchestrator and demo microservices.

### What’s implemented

- `backend/chaos-orchestrator` consumes experiment lifecycle events from Kafka.
- `ExperimentEventListener` listens to `experiment.started` and `experiment.stopped`.
- `ChaosInjectionService` resolves target demo services and triggers chaos via internal endpoints.
- Demo services provide internal chaos endpoints:
  - `POST /internal/chaos/timeout`
  - `POST /internal/chaos/cpu-spike`
  - `POST /internal/chaos/reset`
- `ChaosEventPublisher` emits `chaos.injection.completed` after successful injection.

### Fix applied

- Corrected a bug in `backend/chaos-orchestrator/src/main/java/com/chaospilot/orchestrator/service/ChaosInjectionService.java` where timeout injection used an undefined `webClient` instance.

### Key files

- `backend/chaos-orchestrator/src/main/java/com/chaospilot/orchestrator/kafka/ExperimentEventListener.java`
- `backend/chaos-orchestrator/src/main/java/com/chaospilot/orchestrator/service/ChaosInjectionService.java`
- `backend/chaos-orchestrator/src/main/java/com/chaospilot/orchestrator/kafka/ChaosEventPublisher.java`
- `demo-services/order-service/src/main/java/com/chaospilot/order/controller/ChaosController.java`
- `demo-services/payment-service/src/main/java/com/chaospilot/payment/controller/ChaosController.java`
- `demo-services/inventory-service/src/main/java/com/chaospilot/inventory/controller/ChaosController.java`
- `demo-services/user-service/src/main/java/com/chaospilot/user/controller/ChaosController.java`

### Manual test flow

1. Start the full stack with Docker Compose.
2. Create an experiment via `POST /api/experiments`.
3. Start it via `POST /api/experiments/{id}/start`.
4. Confirm the orchestrator calls the target service internal chaos endpoint.
5. Stop the experiment via `POST /api/experiments/{id}/stop` and confirm reset.
