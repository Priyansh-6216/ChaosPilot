package com.chaospilot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChaosGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChaosGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("experiment-service", r -> r
                        .path("/api/experiments/**")
                        .uri("http://experiment-service:8081"))
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("http://order-service:8083"))
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .uri("http://payment-service:8084"))
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .uri("http://inventory-service:8085"))
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("http://user-service:8086"))
                .build();
    }
}
