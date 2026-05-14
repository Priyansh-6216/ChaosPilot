package com.chaospilot.experiment.model;

public enum FailureType {
    LATENCY,
    TIMEOUT,
    SERVICE_CRASH,
    CPU_SPIKE,
    MEMORY_PRESSURE,
    DB_SLOWDOWN,
    KAFKA_LAG,
    HTTP_500_ERROR
}
