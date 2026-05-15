package com.chaospilot.orchestrator.service;

import com.chaospilot.orchestrator.kafka.ChaosEventPublisher;
import com.chaospilot.orchestrator.model.ExperimentEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChaosInjectionService {

    private static final Logger log = LoggerFactory.getLogger(ChaosInjectionService.class);
    private final Builder webClientBuilder;
    private final ChaosEventPublisher publisher;

    @Value("${chaos.orchestrator.order-service-url:http://order-service:8083}")
    private String orderServiceUrl;

    @Value("${chaos.orchestrator.payment-service-url:http://payment-service:8084}")
    private String paymentServiceUrl;

    @Value("${chaos.orchestrator.inventory-service-url:http://inventory-service:8085}")
    private String inventoryServiceUrl;

    @Value("${chaos.orchestrator.user-service-url:http://user-service:8086}")
    private String userServiceUrl;

    public void injectChaos(ExperimentEvent event) {
        String baseUrl = resolveTargetUrl(event.getTargetService());
        if (baseUrl == null) {
            log.warn("Unknown target service for chaos injection: {}", event.getTargetService());
            return;
        }

        try {
            if ("CPU_SPIKE".equalsIgnoreCase(event.getFailureType())) {
                log.info("Triggering CPU spike for {}", event.getTargetService());
                webClientBuilder.build()
                        .post()
                        .uri(baseUrl + "/internal/chaos/cpu-spike")
                        .bodyValue(Map.of(
                                "enabled", true,
                                "durationSeconds", event.getDurationSeconds() != null ? event.getDurationSeconds() : 60
                        ))
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block(Duration.ofSeconds(10));
            } else {
                log.info("Triggering timeout fault for {}", event.getTargetService());
                webClientBuilder.build()
                        .post()
                        .uri(baseUrl + "/internal/chaos/timeout")
                        .bodyValue(Map.of("enabled", true))
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block(Duration.ofSeconds(10));
            }
            publisher.publishChaosInjectionCompleted(event);
        } catch (WebClientResponseException ex) {
            log.error("Chaos injection failed for {}: {}", event.getTargetService(), ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error during chaos injection for {}", event.getTargetService(), ex);
        }
    }

    public void resetChaos(ExperimentEvent event) {
        String baseUrl = resolveTargetUrl(event.getTargetService());
        if (baseUrl == null) {
            log.warn("Unknown target service for chaos reset: {}", event.getTargetService());
            return;
        }

        try {
            log.info("Resetting chaos on {}", event.getTargetService());
            webClientBuilder.build()
                    .post()
                    .uri(baseUrl + "/internal/chaos/reset")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block(Duration.ofSeconds(10));
        } catch (WebClientResponseException ex) {
            log.error("Chaos reset failed for {}: {}", event.getTargetService(), ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error during chaos reset for {}", event.getTargetService(), ex);
        }
    }

    private String resolveTargetUrl(String targetService) {
        if (targetService == null) {
            return null;
        }
        return switch (targetService.toLowerCase()) {
            case "order-service" -> orderServiceUrl;
            case "payment-service" -> paymentServiceUrl;
            case "inventory-service" -> inventoryServiceUrl;
            case "user-service" -> userServiceUrl;
            default -> null;
        };
    }
}
