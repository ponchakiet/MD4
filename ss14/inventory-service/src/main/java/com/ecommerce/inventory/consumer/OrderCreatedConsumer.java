package com.ecommerce.inventory.consumer;

import com.ecommerce.inventory.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedConsumer {

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void consume(OrderCreatedEvent event) {
        log.info("Inventory Service received event: {}", event);
        log.info("Reducing stock for product id {} by quantity {} for order id {}", event.getProductId(), event.getQuantity(), event.getOrderId());
    }
}
