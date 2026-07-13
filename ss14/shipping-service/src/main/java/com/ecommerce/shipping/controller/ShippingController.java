package com.ecommerce.shipping.controller;

import com.ecommerce.shipping.event.ShippingStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/deliver/{orderId}")
    public ResponseEntity<String> deliverOrder(@PathVariable Long orderId) {
        ShippingStatusEvent event = ShippingStatusEvent.builder()
                .orderId(orderId)
                .status("DELIVERED")
                .build();
        
        kafkaTemplate.send("shipping-events", event);
        return ResponseEntity.ok("Shipping status updated to DELIVERED and event published for order: " + orderId);
    }
}
