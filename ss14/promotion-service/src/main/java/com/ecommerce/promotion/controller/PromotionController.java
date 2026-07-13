package com.ecommerce.promotion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final StringRedisTemplate redisTemplate;

    @PostMapping("/update/{productId}")
    public ResponseEntity<String> updatePromotion(@PathVariable Long productId) {
        // Publish product_id to channel promotion-updates
        redisTemplate.convertAndSend("promotion-updates", String.valueOf(productId));
        return ResponseEntity.ok("Promotion updated and message published for product ID: " + productId);
    }
}
