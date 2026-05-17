package com.chaospilot.experiment.kafka;

import com.chaospilot.experiment.model.ChaosInjectionRequest;
import com.chaospilot.experiment.model.ExperimentEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExperimentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ExperimentEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishExperimentCreated(ExperimentEvent event) {
        publish("experiment.created", event);
    }

    public void publishExperimentStarted(ExperimentEvent event) {
        publish("experiment.started", event);
    }

    public void publishExperimentStopped(ExperimentEvent event) {
        publish("experiment.stopped", event);
    }

    public void publishExperimentCompleted(ExperimentEvent event) {
        publish("experiment.completed", event);
    }

    public void publishChaosInjectionRequested(ChaosInjectionRequest request) {
        publish("chaos.injection.requested", request);
    }

    private void publish(String topic, Object payload) {
        log.info("Publishing event to {}: {}", topic, payload);
        kafkaTemplate.send(topic, payload);
    }
}
