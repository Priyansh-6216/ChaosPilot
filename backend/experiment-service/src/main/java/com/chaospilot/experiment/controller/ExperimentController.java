package com.chaospilot.experiment.controller;

import com.chaospilot.experiment.dto.CreateExperimentRequest;
import com.chaospilot.experiment.dto.ExperimentResponse;
import com.chaospilot.experiment.service.ExperimentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiments")
@RequiredArgsConstructor
public class ExperimentController {

    private final ExperimentService experimentService;

    @PostMapping
    public ResponseEntity<ExperimentResponse> createExperiment(
            @Valid @RequestBody CreateExperimentRequest request) {
        ExperimentResponse response = experimentService.createExperiment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExperimentResponse>> listExperiments() {
        return ResponseEntity.ok(experimentService.listExperiments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperimentResponse> getExperiment(@PathVariable UUID id) {
        return ResponseEntity.ok(experimentService.getExperiment(id));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ExperimentResponse> startExperiment(@PathVariable UUID id) {
        return ResponseEntity.ok(experimentService.startExperiment(id));
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<ExperimentResponse> stopExperiment(@PathVariable UUID id) {
        return ResponseEntity.ok(experimentService.stopExperiment(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperiment(@PathVariable UUID id) {
        experimentService.deleteExperiment(id);
        return ResponseEntity.noContent().build();
    }
}
