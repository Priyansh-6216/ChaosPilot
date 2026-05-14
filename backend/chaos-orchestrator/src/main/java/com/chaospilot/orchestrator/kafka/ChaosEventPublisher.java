package com.chaospilot.orchestrator.kafka;

import com.chaospilot.orchestrator.model.ExperimentEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChaosEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ChaosEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishChaosInjectionCompleted(ExperimentEvent event) {
        log.info("Publishing chaos injection completed event for experiment {}", event.getExperimentId());
        kafkaTemplate.send("chaos.injection.completed", event);
    }
}
