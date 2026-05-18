# ChaosPilot Day 6 Summary

## Day 6 - Report Service

This day implements the report service API for retrieving generated chaos experiment reports.

### What’s implemented

- Added `report-service` report model for the `chaos_reports` table.
- Implemented a `GET /api/reports` endpoint to list all available chaos reports.
- Implemented a `GET /api/reports/{experimentId}` endpoint to retrieve a single experiment report.
- Added JSONB mapping support for `blast_radius` and `recommended_fixes` using `hibernate-types-52`.
- Added a consistent error handler to return `404 NOT_FOUND` when a report is not available.

### Files created

- `backend/report-service/src/main/java/com/chaospilot/report/model/ChaosReport.java`
- `backend/report-service/src/main/java/com/chaospilot/report/repository/ChaosReportRepository.java`
- `backend/report-service/src/main/java/com/chaospilot/report/service/ReportService.java`
- `backend/report-service/src/main/java/com/chaospilot/report/controller/ReportController.java`
- `backend/report-service/src/main/java/com/chaospilot/report/dto/ReportResponse.java`
- `backend/report-service/src/main/java/com/chaospilot/report/exception/NotFoundException.java`
- `backend/report-service/src/main/java/com/chaospilot/report/exception/RestExceptionHandler.java`
- `backend/report-service/pom.xml`

### How to verify

- Start the stack with the infrastructure and services.
- Confirm report service health:
  ```bash
  curl http://localhost:8091/health
  ```
- List reports:
  ```bash
  curl http://localhost:8091/api/reports
  ```
- Retrieve a specific report:
  ```bash
  curl http://localhost:8091/api/reports/<experiment-id>
  ```

### Notes

- This service reads persisted report records from PostgreSQL and exposes them via the public report API.
- Report generation is expected to be handled by the AI RCA worker, which stores outputs in `chaos_reports`.
