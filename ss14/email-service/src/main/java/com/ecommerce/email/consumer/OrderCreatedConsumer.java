package com.ecommerce.email.consumer;

import com.ecommerce.email.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedConsumer {

    @KafkaListener(topics = "order-events", groupId = "email-group")
    public void consume(OrderCreatedEvent event) {
        log.info("Email Service received event: {}", event);
        log.info("Sending confirmation email to customer {} for order id {}", event.getCustomerId(), event.getOrderId());
    }
}
