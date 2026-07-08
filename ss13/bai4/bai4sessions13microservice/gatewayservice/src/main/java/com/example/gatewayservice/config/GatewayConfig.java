package com.example.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route cho Identity Service
                .route("identity-service", r -> r
                        .path("/identity/**")
                        .filters(f -> f
                                .stripPrefix(1)  // Loại bỏ /identity
                                .addRequestHeader("X-Gateway-Request", "true")
                                .addRequestHeader("X-Gateway-Service", "identity-service")
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR)
                                )
                        )
                        .uri("http://localhost:8080")
                )
                // Route test
                .route("test-route", r -> r
                        .path("/test/**")
                        .filters(f -> f
                                .setPath("/api/products/test")
                                .addRequestHeader("X-Test-Header", "gateway-test")
                        )
                        .uri("http://localhost:8080")
                )
                .build();
    }
}