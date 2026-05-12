-- PostgreSQL Database Schema for ChaosPilot

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Experiments table
CREATE TABLE IF NOT EXISTS experiments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    target_service VARCHAR(100) NOT NULL,
    failure_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    duration_seconds INTEGER NOT NULL,
    intensity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    created_by VARCHAR(100),
    metadata JSONB
);

-- Experiment events table for audit trail
CREATE TABLE IF NOT EXISTS experiment_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    experiment_id UUID NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Registered services table
CREATE TABLE IF NOT EXISTS registered_services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    url VARCHAR(500) NOT NULL,
    health_endpoint VARCHAR(255),
    chaos_endpoint VARCHAR(255),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_health_check TIMESTAMP
);

-- Failure injections table
CREATE TABLE IF NOT EXISTS failure_injections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    experiment_id UUID NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
    service_name VARCHAR(100) NOT NULL,
    injection_type VARCHAR(50) NOT NULL,
    parameters JSONB,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    removed_at TIMESTAMP
);

-- Chaos reports table
CREATE TABLE IF NOT EXISTS chaos_reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    experiment_id UUID NOT NULL UNIQUE REFERENCES experiments(id) ON DELETE CASCADE,
    summary TEXT,
    root_cause TEXT,
    severity VARCHAR(50),
    resilience_score INTEGER,
    blast_radius JSONB,
    recommended_fixes JSONB,
    prevention_plan TEXT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Service metrics snapshot (for comparison)
CREATE TABLE IF NOT EXISTS metrics_snapshot (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    experiment_id UUID NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
    service_name VARCHAR(100) NOT NULL,
    metric_type VARCHAR(100) NOT NULL,
    metric_value FLOAT NOT NULL,
    phase VARCHAR(50), -- BEFORE, DURING, AFTER
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_experiments_status ON experiments(status);
CREATE INDEX IF NOT EXISTS idx_experiments_target_service ON experiments(target_service);
CREATE INDEX IF NOT EXISTS idx_experiments_created_at ON experiments(created_at);
CREATE INDEX IF NOT EXISTS idx_experiment_events_experiment_id ON experiment_events(experiment_id);
CREATE INDEX IF NOT EXISTS idx_failure_injections_experiment_id ON failure_injections(experiment_id);
CREATE INDEX IF NOT EXISTS idx_failure_injections_service_name ON failure_injections(service_name);
CREATE INDEX IF NOT EXISTS idx_chaos_reports_experiment_id ON chaos_reports(experiment_id);
CREATE INDEX IF NOT EXISTS idx_metrics_snapshot_experiment_id ON metrics_snapshot(experiment_id);
CREATE INDEX IF NOT EXISTS idx_metrics_snapshot_service_name ON metrics_snapshot(service_name);

-- Insert default registered services
INSERT INTO registered_services (name, url, health_endpoint, chaos_endpoint) VALUES
    ('order-service', 'http://order-service:8083', '/health', '/internal/chaos'),
    ('payment-service', 'http://payment-service:8084', '/health', '/internal/chaos'),
    ('inventory-service', 'http://inventory-service:8085', '/health', '/internal/chaos'),
    ('user-service', 'http://user-service:8086', '/health', '/internal/chaos')
ON CONFLICT (name) DO NOTHING;
