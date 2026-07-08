package com.example.gatewayservice.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("🚀 Gateway Request: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI()
        );

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    long endTime = System.currentTimeMillis();
                    log.info("✅ Gateway Response: {} - Duration: {}ms",
                            exchange.getResponse().getStatusCode(),
                            (endTime - startTime)
                    );
                })
        );
    }

    @Override
    public int getOrder() {
        return -1; // Chạy trước các filter khác
    }
}