package com.ecommerce.notification.consumer;

import com.ecommerce.notification.event.ShippingStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShippingConsumer {

    @KafkaListener(topics = "shipping-events", groupId = "notification-group")
    public void consume(ShippingStatusEvent event) {
        log.info("Notification Service received event: {}", event);
        if ("DELIVERED".equals(event.getStatus())) {
            log.info("Congratulating customer! Order id {} has been delivered successfully.", event.getOrderId());
        }
    }
}
