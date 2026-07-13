package com.example.gatewayservice.filter;

import com.example.gatewayservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Lấy path
        String path = request.getURI().getPath();

        // Chỉ filter các request đến /api/courses/**
        if (!path.startsWith("/api/courses/")) {
            return chain.filter(exchange);
        }

        // Lấy token từ header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }

        // Extract role
        String role = jwtUtil.extractRole(token);

        // Thêm header mới
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Role", role)
                .build();

        return chain.filter(exchange.mutate()
                .request(mutatedRequest)
                .build());
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = String.format("""
                {
                    "timestamp": "%s",
                    "status": 401,
                    "error": "Unauthorized",
                    "message": "%s"
                }
                """, java.time.LocalDateTime.now(), message);

        DataBuffer buffer = response.bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}