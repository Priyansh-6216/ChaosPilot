package com.chaospilot.order.controller;

import com.chaospilot.order.dto.CpuSpikeRequest;
import com.chaospilot.order.dto.ChaosToggleRequest;
import com.chaospilot.order.service.ChaosStateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChaosController {

    private final ChaosStateService chaosStateService;

    @PostMapping("/internal/chaos/timeout")
    public ResponseEntity<Map<String, String>> enableTimeout(@Valid @RequestBody ChaosToggleRequest request) {
        chaosStateService.enableTimeout(request.getEnabled());
        String message = request.getEnabled() ? "Timeout chaos enabled" : "Timeout chaos disabled";
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", message));
    }

    @PostMapping("/internal/chaos/cpu-spike")
    public ResponseEntity<Map<String, String>> applyCpuSpike(@Valid @RequestBody CpuSpikeRequest request) {
        if (request.getEnabled() && request.getDurationSeconds() == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", "durationSeconds is required when enabled is true"));
        }
        chaosStateService.enableCpuSpike(request.getEnabled(), request.getEnabled() ? request.getDurationSeconds() : 0);
        String message = request.getEnabled() ? "CPU spike enabled" : "CPU spike disabled";
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", message));
    }

    @PostMapping("/internal/chaos/reset")
    public ResponseEntity<Map<String, String>> resetChaos() {
        chaosStateService.reset();
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "All chaos injections reset"));
    }
}
