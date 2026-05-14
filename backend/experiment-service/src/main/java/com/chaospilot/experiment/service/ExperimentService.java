package com.chaospilot.experiment.service;

import com.chaospilot.experiment.dto.CreateExperimentRequest;
import com.chaospilot.experiment.dto.ExperimentResponse;
import com.chaospilot.experiment.exception.InvalidExperimentStateException;
import com.chaospilot.experiment.exception.NotFoundException;
import com.chaospilot.experiment.kafka.ExperimentEventPublisher;
import com.chaospilot.experiment.model.ChaosInjectionRequest;
import com.chaospilot.experiment.model.Experiment;
import com.chaospilot.experiment.model.ExperimentEvent;
import com.chaospilot.experiment.model.ExperimentStatus;
import com.chaospilot.experiment.repository.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperimentService {

    private final ExperimentRepository repository;
    private final ExperimentEventPublisher publisher;

    @Transactional
    public ExperimentResponse createExperiment(CreateExperimentRequest request) {
        Experiment experiment = Experiment.builder()
                .name(request.getName())
                .targetService(request.getTargetService())
                .failureType(request.getFailureType())
                .status(ExperimentStatus.CREATED)
                .durationSeconds(request.getDurationSeconds())
                .intensity(request.getIntensity())
                .build();

        experiment = repository.save(experiment);
        publisher.publishExperimentCreated(buildEvent(experiment, "experiment.created"));
        return toResponse(experiment);
    }

    @Transactional(readOnly = true)
    public List<ExperimentResponse> listExperiments() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExperimentResponse getExperiment(UUID id) {
        Experiment experiment = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Experiment with id " + id + " not found"));
        return toResponse(experiment);
    }

    @Transactional
    public ExperimentResponse startExperiment(UUID id) {
        Experiment experiment = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Experiment with id " + id + " not found"));
        if (experiment.getStatus() != ExperimentStatus.CREATED) {
            throw new InvalidExperimentStateException("Only CREATED experiments can be started");
        }
        experiment.setStatus(ExperimentStatus.RUNNING);
        experiment.setStartedAt(LocalDateTime.now());
        repository.save(experiment);

        publisher.publishExperimentStarted(buildEvent(experiment, "experiment.started"));
        publisher.publishChaosInjectionRequested(ChaosInjectionRequest.builder()
                .experimentId(experiment.getId())
                .targetService(experiment.getTargetService())
                .failureType(experiment.getFailureType())
                .durationSeconds(experiment.getDurationSeconds())
                .intensity(experiment.getIntensity())
                .build());

        return toResponse(experiment);
    }

    @Transactional
    public ExperimentResponse stopExperiment(UUID id) {
        Experiment experiment = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Experiment with id " + id + " not found"));
        if (experiment.getStatus() != ExperimentStatus.RUNNING) {
            throw new InvalidExperimentStateException("Only RUNNING experiments can be stopped");
        }
        experiment.setStatus(ExperimentStatus.STOPPED);
        experiment.setEndedAt(LocalDateTime.now());
        repository.save(experiment);

        publisher.publishExperimentStopped(buildEvent(experiment, "experiment.stopped"));
        return toResponse(experiment);
    }

    @Transactional
    public void deleteExperiment(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Experiment with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    private ExperimentResponse toResponse(Experiment experiment) {
        return ExperimentResponse.builder()
                .id(experiment.getId())
                .name(experiment.getName())
                .targetService(experiment.getTargetService())
                .failureType(experiment.getFailureType())
                .status(experiment.getStatus())
                .durationSeconds(experiment.getDurationSeconds())
                .intensity(experiment.getIntensity())
                .createdAt(experiment.getCreatedAt())
                .startedAt(experiment.getStartedAt())
                .endedAt(experiment.getEndedAt())
                .build();
    }

    private ExperimentEvent buildEvent(Experiment experiment, String eventType) {
        return ExperimentEvent.builder()
                .experimentId(experiment.getId())
                .eventType(eventType)
                .name(experiment.getName())
                .targetService(experiment.getTargetService())
                .failureType(experiment.getFailureType())
                .status(experiment.getStatus())
                .durationSeconds(experiment.getDurationSeconds())
                .intensity(experiment.getIntensity())
                .createdAt(experiment.getCreatedAt())
                .startedAt(experiment.getStartedAt())
                .endedAt(experiment.getEndedAt())
                .build();
    }
}
