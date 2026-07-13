package com.ecommerce.order.consumer;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.event.ShippingStatusEvent;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShippingStatusConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "shipping-events", groupId = "order-group")
    public void consume(ShippingStatusEvent event) {
        log.info("Order Service received Shipping Status Event: {}", event);
        if ("DELIVERED".equals(event.getStatus())) {
            orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
                order.setStatus("COMPLETED");
                orderRepository.save(order);
                log.info("Order status updated to COMPLETED for order id: {}", order.getId());
            }, () -> {
                log.error("Order not found with id: {}", event.getOrderId());
            });
        }
    }
}
