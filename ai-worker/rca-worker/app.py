import json
import logging
import subprocess
import threading
import uuid
from datetime import datetime

from fastapi import FastAPI
from kafka import KafkaConsumer
from pydantic import BaseModel, BaseSettings
from sqlalchemy import (Column, DateTime, Integer, String, Text, create_engine,
                        insert)
from sqlalchemy.dialects.postgresql import JSONB, UUID as PG_UUID
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
logger = logging.getLogger("rca-worker")

Base = declarative_base()


class Settings(BaseSettings):
    database_url: str = "postgresql://chaospilot:chaospilot123@postgres:5432/chaospilot"
    kafka_bootstrap_servers: str = "kafka:29092"
    kafka_topic: str = "experiment.completed"
    kafka_group_id: str = "rca-worker-group"
    ollama_model: str = "llama2"
    use_ollama: bool = False
    ollama_command: str = "ollama"

    class Config:
        env_file = ".env"


class ChaosReport(Base):
    __tablename__ = "chaos_reports"

    id = Column(PG_UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    experiment_id = Column(PG_UUID(as_uuid=True), unique=True, nullable=False)
    summary = Column(Text, nullable=True)
    root_cause = Column(Text, nullable=True)
    severity = Column(String(50), nullable=True)
    resilience_score = Column(Integer, nullable=True)
    blast_radius = Column(JSONB, nullable=True)
    recommended_fixes = Column(JSONB, nullable=True)
    prevention_plan = Column(Text, nullable=True)
    generated_at = Column(DateTime, default=datetime.utcnow, nullable=False)


class ExperimentCompletedEvent(BaseModel):
    experimentId: uuid.UUID
    eventType: str
    name: str
    targetService: str
    failureType: str
    status: str
    durationSeconds: int | None = None
    intensity: int | None = None
    createdAt: str | None = None
    startedAt: str | None = None
    endedAt: str | None = None
    message: str | None = None


settings = Settings()
engine = create_engine(settings.database_url, future=True)
SessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False, future=True)

Base.metadata.create_all(bind=engine)

app = FastAPI(title="ChaosPilot RCA Worker")


def build_analysis_prompt(event: ExperimentCompletedEvent) -> str:
    return (
        f"You are an AI root cause analysis engine for a chaos engineering platform. "
        f"A chaos experiment completed and the system now needs a concise RCA report.\n\n"
        f"Experiment details:\n"
        f"- Name: {event.name}\n"
        f"- Target service: {event.targetService}\n"
        f"- Failure type: {event.failureType}\n"
        f"- Status: {event.status}\n"
        f"- Duration seconds: {event.durationSeconds or 'unknown'}\n"
        f"- Intensity: {event.intensity or 'unknown'}\n"
        f"- Started at: {event.startedAt or 'unknown'}\n"
        f"- Ended at: {event.endedAt or 'unknown'}\n"
        f"- Created at: {event.createdAt or 'unknown'}\n\n"
        f"Generate a JSON object with the following keys: summary, root_cause, severity, resilience_score, blast_radius, recommended_fixes, prevention_plan. "
        f"The summary should be brief. Severity should be one of LOW, MEDIUM, HIGH, CRITICAL. "
        f"Resilience_score should be an integer between 1 and 100. "
        f"Blast_radius should describe impacted services and system areas. "
        f"Recommended_fixes should be a JSON list of action items. "
        f"Do not include any additional keys outside the requested schema."
    )


def call_ollama(prompt: str) -> str:
    if not settings.use_ollama:
        raise RuntimeError("OLLAMA integration is disabled")

    command = [settings.ollama_command, "predict", settings.ollama_model]
    logger.info("Calling Ollama model %s", settings.ollama_model)
    result = subprocess.run(
        command,
        input=prompt,
        text=True,
        capture_output=True,
        timeout=120,
    )
    if result.returncode != 0:
        raise RuntimeError(
            f"Ollama prediction failed: {result.stderr.strip() or result.stdout.strip()}"
        )

    output = result.stdout.strip()
    if not output:
        raise RuntimeError("Ollama returned empty output")
    return output


def fallback_analysis(event: ExperimentCompletedEvent) -> dict:
    severity = "LOW"
    if event.failureType in {"CPU_SPIKE", "TIMEOUT", "ERROR"}:
        severity = "MEDIUM"
    if event.failureType in {"SERVICE_CRASH", "DATABASE_FAILURE"}:
        severity = "HIGH"
    if event.intensity and event.intensity >= 80:
        severity = "CRITICAL"

    return {
        "summary": (
            f"The chaos experiment on {event.targetService} completed with failure type {event.failureType}. "
            f"The incident impacted service latency and should be investigated for resilience gaps."
        ),
        "root_cause": (
            f"The failure was caused by an injected {event.failureType.lower()} scenario on {event.targetService}, "
            f"which exposed a resilience gap in service recovery and timeout handling."
        ),
        "severity": severity,
        "resilience_score": max(1, min(100, 100 - (event.intensity or 0))),
        "blast_radius": {
            "affected_service": event.targetService,
            "failure_type": event.failureType,
            "components": [event.targetService, "experiment-service", "chaos-orchestrator"],
        },
        "recommended_fixes": [
            "Add retry/backoff handling for the target service",
            "Improve timeout thresholds and circuit breaking",
            "Add better alerting for service degradation during chaos experiments",
        ],
        "prevention_plan": (
            "Review the experiment outcome, harden the target service fault handling, "
            "and add automated regression tests for failure scenarios."
        ),
    }


def analyze_event(event_payload: dict) -> dict:
    try:
        event = ExperimentCompletedEvent(**event_payload)
    except Exception as exc:
        logger.error("Invalid experiment.completed payload: %s", exc)
        raise

    prompt = build_analysis_prompt(event)
    logger.info("Building root cause analysis for experiment %s", event.experimentId)

    if settings.use_ollama:
        try:
            output = call_ollama(prompt)
            logger.info("Ollama output received")
            parsed = json.loads(output)
            return parsed
        except Exception as exc:
            logger.warning("Ollama analysis failed, falling back to heuristic analysis: %s", exc)

    return fallback_analysis(event)


def persist_report(event_payload: dict, analysis: dict) -> None:
    event = ExperimentCompletedEvent(**event_payload)
    with SessionLocal() as session:
        try:
            existing = (
                session.query(ChaosReport)
                .filter(ChaosReport.experiment_id == event.experimentId)
                .one_or_none()
            )
            if existing is None:
                report = ChaosReport(
                    experiment_id=event.experimentId,
                    summary=analysis.get("summary"),
                    root_cause=analysis.get("root_cause"),
                    severity=analysis.get("severity"),
                    resilience_score=analysis.get("resilience_score"),
                    blast_radius=analysis.get("blast_radius"),
                    recommended_fixes=analysis.get("recommended_fixes"),
                    prevention_plan=analysis.get("prevention_plan"),
                    generated_at=datetime.utcnow(),
                )
            else:
                report = existing
                report.summary = analysis.get("summary")
                report.root_cause = analysis.get("root_cause")
                report.severity = analysis.get("severity")
                report.resilience_score = analysis.get("resilience_score")
                report.blast_radius = analysis.get("blast_radius")
                report.recommended_fixes = analysis.get("recommended_fixes")
                report.prevention_plan = analysis.get("prevention_plan")
                report.generated_at = datetime.utcnow()

            session.add(report)
            session.commit()
            logger.info("Persisted RCA report for experiment %s", event.experimentId)
        except SQLAlchemyError as exc:
            session.rollback()
            logger.exception("Failed to persist RCA report: %s", exc)
            raise


def consume_experiment_completed() -> None:
    logger.info("Starting Kafka consumer for topic %s", settings.kafka_topic)
    consumer = KafkaConsumer(
        settings.kafka_topic,
        bootstrap_servers=[settings.kafka_bootstrap_servers],
        group_id=settings.kafka_group_id,
        auto_offset_reset="earliest",
        enable_auto_commit=True,
        value_deserializer=lambda m: json.loads(m.decode("utf-8")) if m else None,
    )

    for message in consumer:
        if not message.value:
            continue
        try:
            analysis = analyze_event(message.value)
            persist_report(message.value, analysis)
        except Exception as exc:
            logger.exception("Error processing experiment.completed message: %s", exc)


@app.on_event("startup")
def startup_event():
    thread = threading.Thread(target=consume_experiment_completed, daemon=True)
    thread.start()
    logger.info("RCA worker startup complete")


@app.get("/health")
def health() -> dict:
    return {"status": "UP"}
