# ChaosPilot Day 2 Summary

## Day 2 - Experiment Management APIs

This day focuses on implementing the Experiment Management service and the first event-driven chaos orchestration behavior.

### What’s implemented

- `backend/experiment-service` contains the Day 2 service implementation.
- Experiment CRUD API and lifecycle endpoints are available in `ExperimentController`:
  - `POST /api/experiments`
  - `GET /api/experiments`
  - `GET /api/experiments/{id}`
  - `POST /api/experiments/{id}/start`
  - `POST /api/experiments/{id}/stop`
  - `DELETE /api/experiments/{id}`
- `ExperimentService` provides business logic for:
  - creating experiments
  - listing experiments
  - retrieving experiments
  - starting experiments
  - stopping experiments
  - deleting experiments
- Kafka events are published through `ExperimentEventPublisher` for:
  - `experiment.created`
  - `experiment.started`
  - `experiment.stopped`
  - `chaos.injection.requested`
- Domain model support exists with `Experiment`, `ExperimentStatus`, `FailureType`, and `ChaosInjectionRequest`.

### Key files

- `backend/experiment-service/src/main/java/com/chaospilot/experiment/model/Experiment.java`
- `backend/experiment-service/src/main/java/com/chaospilot/experiment/repository/ExperimentRepository.java`
- `backend/experiment-service/src/main/java/com/chaospilot/experiment/service/ExperimentService.java`
- `backend/experiment-service/src/main/java/com/chaospilot/experiment/controller/ExperimentController.java`
- `backend/experiment-service/src/main/java/com/chaospilot/experiment/kafka/ExperimentEventPublisher.java`
- `backend/experiment-service/src/main/java/com/chaospilot/experiment/dto/CreateExperimentRequest.java`
- `backend/experiment-service/src/main/java/com/chaospilot/experiment/dto/ExperimentResponse.java`

### Build / verification

Run from the repository root:

```bash
mvn -pl backend/experiment-service -am -DskipITs test
```

> Note: this environment currently does not have `mvn` installed, so the build could not be executed here.

### Next Day direction

- Continue to Day 3 by implementing the chaos injection execution engine in `chaos-orchestrator` and demo services.
- Add more event topics and end-to-end verification for `chaos.injection.requested` and `chaos.injection.completed`.
- Add Postman / curl examples for Experiment CRUD and start/stop flows.
