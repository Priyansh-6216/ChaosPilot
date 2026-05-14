package com.chaospilot.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CpuSpikeRequest {

    @NotNull(message = "enabled is required")
    private Boolean enabled;

    @Min(value = 1, message = "durationSeconds must be at least 1")
    private Integer durationSeconds;
}
