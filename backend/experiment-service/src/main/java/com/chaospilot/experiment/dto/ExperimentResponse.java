package com.chaospilot.experiment.dto;

import com.chaospilot.experiment.model.ExperimentStatus;
import com.chaospilot.experiment.model.FailureType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExperimentResponse {
    private UUID id;
    private String name;
    private String targetService;
    private FailureType failureType;
    private ExperimentStatus status;
    private Integer durationSeconds;
    private Integer intensity;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
