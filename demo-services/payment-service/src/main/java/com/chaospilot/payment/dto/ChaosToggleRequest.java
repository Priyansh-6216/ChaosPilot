package com.chaospilot.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChaosToggleRequest {

    @NotNull(message = "enabled is required")
    private Boolean enabled;
}
