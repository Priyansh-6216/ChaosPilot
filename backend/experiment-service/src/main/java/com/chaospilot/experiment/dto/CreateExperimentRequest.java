package com.chaospilot.experiment.dto;

import com.chaospilot.experiment.model.FailureType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateExperimentRequest {

    @NotBlank(message = "Experiment name is required")
    private String name;

    @NotBlank(message = "Target service is required")
    private String targetService;

    @NotNull(message = "Failure type is required")
    private FailureType failureType;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 second")
    private Integer durationSeconds;

    @NotNull(message = "Intensity is required")
    @Min(value = 1, message = "Intensity must be at least 1")
    @Max(value = 100, message = "Intensity must be 100 or less")
    private Integer intensity;
}
