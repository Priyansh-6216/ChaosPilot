package com.chaospilot.orchestrator.kafka;

import com.chaospilot.orchestrator.model.ExperimentEvent;
import com.chaospilot.orchestrator.service.ChaosInjectionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExperimentEventListener {

    private static final Logger log = LoggerFactory.getLogger(ExperimentEventListener.class);
    private final ChaosInjectionService chaosInjectionService;

    @KafkaListener(topics = "experiment.started", groupId = "chaos-orchestrator-group",
            containerFactory = "experimentEventKafkaListenerContainerFactory")
    public void handleExperimentStarted(ExperimentEvent event) {
        log.info("Received experiment.started event for {} -> {}", event.getExperimentId(), event.getTargetService());
        chaosInjectionService.injectChaos(event);
    }

    @KafkaListener(topics = "experiment.stopped", groupId = "chaos-orchestrator-group",
            containerFactory = "experimentEventKafkaListenerContainerFactory")
    public void handleExperimentStopped(ExperimentEvent event) {
        log.info("Received experiment.stopped event for {} -> {}", event.getExperimentId(), event.getTargetService());
        chaosInjectionService.resetChaos(event);
    }
}
