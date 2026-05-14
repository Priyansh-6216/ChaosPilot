package com.chaospilot.experiment.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChaosInjectionRequest {
    private UUID experimentId;
    private String targetService;
    private FailureType failureType;
    private Integer durationSeconds;
    private Integer intensity;
}
