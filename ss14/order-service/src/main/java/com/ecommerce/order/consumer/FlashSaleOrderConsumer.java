package com.ecommerce.order.consumer;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.event.FlashSaleOrderEvent;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class FlashSaleOrderConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "flash-sale-events", groupId = "flash-sale-group")
    public void consume(FlashSaleOrderEvent event) {
        log.info("Order Service received Flash Sale Order Event: {}", event);
        try {
            Order order = Order.builder()
                    .customerId(event.getCustomerId())
                    .productId(event.getProductId())
                    .orderDate(LocalDateTime.now())
                    .totalAmount(event.getTotalAmount())
                    .status("COMPLETED")
                    .build();
            
            Order savedOrder = orderRepository.save(order);
            log.info("Flash sale order saved successfully in DB with id: {}", savedOrder.getId());
        } catch (Exception e) {
            log.error("Failed to save flash sale order in DB", e);
        }
    }
}
