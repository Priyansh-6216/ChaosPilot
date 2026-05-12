# API Contracts

## Experiment Service APIs

### Create Experiment

**Request:**
```
POST /api/experiments
Content-Type: application/json

{
  "name": "Test latency on payment-service",
  "targetService": "payment-service",
  "failureType": "LATENCY",
  "durationSeconds": 120,
  "intensity": 70
}
```

**Response (201 Created):**
```json
{
  "id": "uuid-123",
  "name": "Test latency on payment-service",
  "targetService": "payment-service",
  "failureType": "LATENCY",
  "status": "CREATED",
  "durationSeconds": 120,
  "intensity": 70,
  "createdAt": "2026-05-12T10:00:00Z"
}
```

### List Experiments

**Request:**
```
GET /api/experiments
```

**Response (200 OK):**
```json
{
  "experiments": [
    {
      "id": "uuid-123",
      "name": "Test latency on payment-service",
      "targetService": "payment-service",
      "failureType": "LATENCY",
      "status": "CREATED",
      "durationSeconds": 120,
      "intensity": 70,
      "createdAt": "2026-05-12T10:00:00Z"
    }
  ],
  "total": 1
}
```

### Get Experiment Details

**Request:**
```
GET /api/experiments/{experimentId}
```

**Response (200 OK):**
```json
{
  "id": "uuid-123",
  "name": "Test latency on payment-service",
  "targetService": "payment-service",
  "failureType": "LATENCY",
  "status": "CREATED",
  "durationSeconds": 120,
  "intensity": 70,
  "createdAt": "2026-05-12T10:00:00Z",
  "startedAt": null,
  "endedAt": null
}
```

### Start Experiment

**Request:**
```
POST /api/experiments/{experimentId}/start
```

**Response (200 OK):**
```json
{
  "id": "uuid-123",
  "status": "RUNNING",
  "startedAt": "2026-05-12T10:01:00Z"
}
```

### Stop Experiment

**Request:**
```
POST /api/experiments/{experimentId}/stop
```

**Response (200 OK):**
```json
{
  "id": "uuid-123",
  "status": "STOPPED",
  "endedAt": "2026-05-12T10:02:00Z"
}
```

### Delete Experiment

**Request:**
```
DELETE /api/experiments/{experimentId}
```

**Response (204 No Content)**

## Failure Types (Day 2)

```
LATENCY - Add artificial delay
TIMEOUT - Extend response time beyond client timeout
SERVICE_CRASH - Simulate service unavailability
CPU_SPIKE - Consume CPU resources
MEMORY_PRESSURE - Consume memory
DB_SLOWDOWN - Slow database queries
KAFKA_LAG - Delay message processing
HTTP_500_ERROR - Return HTTP 500
```

## Experiment Status Transitions

```
CREATED
   ↓
RUNNING
   ↓
STOPPED (or COMPLETED if auto-completed)
   ↓
COMPLETED
```

Alternative: FAILED (if error occurred)

## Kafka Topics

### Experiment Topics

- `experiment.created` - When experiment is created
- `experiment.started` - When experiment transitions to RUNNING
- `experiment.stopped` - When experiment transitions to STOPPED
- `experiment.completed` - When experiment is fully completed
- `experiment.failed` - If experiment failed

### Chaos Topics

- `chaos.injection.requested` - Request to inject chaos
- `chaos.injection.completed` - Chaos successfully injected/removed

## Demo Service Chaos Endpoints

### Enable Latency (All Demo Services)

**Request:**
```
POST /internal/chaos/latency
Content-Type: application/json

{
  "enabled": true,
  "delayMs": 500
}
```

**Response (200 OK):**
```json
{
  "status": "SUCCESS",
  "message": "Latency injection enabled: 500ms"
}
```

### Enable Error Rate

**Request:**
```
POST /internal/chaos/error-rate
Content-Type: application/json

{
  "enabled": true,
  "errorPercentage": 50
}
```

### Enable Timeout

**Request:**
```
POST /internal/chaos/timeout
Content-Type: application/json

{
  "enabled": true
}
```

### Enable CPU Spike

**Request:**
```
POST /internal/chaos/cpu-spike
Content-Type: application/json

{
  "enabled": true,
  "durationSeconds": 60
}
```

### Reset All Chaos

**Request:**
```
POST /internal/chaos/reset
```

**Response (200 OK):**
```json
{
  "status": "SUCCESS",
  "message": "All chaos injections reset"
}
```

## Report Service APIs (Day 6)

### Get Report

**Request:**
```
GET /api/reports/{experimentId}
```

**Response:**
```json
{
  "id": "uuid-456",
  "experimentId": "uuid-123",
  "summary": "Latency injection caused 340% increase in p95 latency",
  "rootCause": "No timeout configured on payment client",
  "severity": "HIGH",
  "resilienceScore": 62,
  "blastRadius": ["order-service", "payment-service"],
  "recommendedFixes": [
    "Add 500ms timeout",
    "Implement circuit breaker",
    "Add fallback state"
  ],
  "preventionPlan": "Deploy Resilience4j circuit breaker",
  "generatedAt": "2026-05-12T10:05:00Z"
}
```

## Error Responses

All services return consistent error format:

```json
{
  "error": "NOT_FOUND",
  "message": "Experiment with id uuid-123 not found",
  "timestamp": "2026-05-12T10:00:00Z"
}
```

Common HTTP Status Codes:
- 200 OK
- 201 Created
- 204 No Content
- 400 Bad Request (invalid input)
- 404 Not Found
- 409 Conflict (invalid state transition)
- 500 Internal Server Error
