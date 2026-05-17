# ChaosPilot Day 5 Summary

## Day 5 - AI Root Cause Analysis (RCA) Worker

This day implements the AI worker that generates root cause summaries after chaos experiments complete.

### What’s implemented

- Added `experiment.completed` Kafka event publication in `backend/experiment-service`.
- Built an RCA worker under `ai-worker/rca-worker`:
  - FastAPI service with a `/health` endpoint
  - Kafka consumer for `experiment.completed`
  - LLM integration using Ollama (optional)
  - Persistence of generated reports to PostgreSQL `chaos_reports`
- Registered the RCA worker in `infra/docker-compose.yml`.

### Files created

- `ai-worker/rca-worker/app.py`
- `ai-worker/rca-worker/requirements.txt`
- `ai-worker/rca-worker/Dockerfile`
- `ai-worker/rca-worker/.dockerignore`
- `DAY5_SUMMARY.md`

### Key behavior

1. When an experiment is stopped, `ExperimentService` publishes `experiment.completed`.
2. The RCA worker consumes that event from Kafka.
3. It builds a root-cause analysis prompt, optionally calls Ollama, and falls back to heuristic analysis if Ollama is unavailable.
4. It stores the resulting report in the `chaos_reports` table.

### How to verify

- Start the stack:
  ```bash
  cd infra
  docker compose up --build
  ```
- Confirm the RCA worker is healthy:
  ```bash
  curl http://localhost:8088/health
  ```
- Run a full experiment and stop it.
- Verify a report row exists in PostgreSQL:
  ```bash
  docker exec -it chaospilot-postgres psql -U chaospilot -d chaospilot -c "SELECT experiment_id, summary FROM chaos_reports LIMIT 5;"
  ```
