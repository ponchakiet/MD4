package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderRequestDTO;
import com.ecommerce.order.dto.OrderResponseDTO;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import com.ecommerce.order.dto.ProductDTO;

import org.springframework.kafka.core.KafkaTemplate;
import com.ecommerce.order.event.OrderCreatedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestClient restClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, RestClient.Builder restClientBuilder, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.restClient = restClientBuilder.build();
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        // Decrement stock in product-service first
        try {
            restClient.put()
                    .uri("http://product-service/api/v1/products/" + requestDTO.getProductId() + "/decrement-stock?quantity=" + requestDTO.getQuantity())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm đã hết hàng");
        }

        ProductDTO product = getProductFromProductService(requestDTO.getProductId());
        BigDecimal productPrice = product.getPrice();
        
        BigDecimal totalAmount = productPrice.multiply(BigDecimal.valueOf(requestDTO.getQuantity()));

        Order order = Order.builder()
                .customerId(requestDTO.getCustomerId())
                .productId(requestDTO.getProductId())
                .orderDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .status("PENDING")
                .build();

        try {
            Order savedOrder = orderRepository.save(order);

            OrderCreatedEvent event = OrderCreatedEvent.builder()
                    .orderId(savedOrder.getId())
                    .customerId(savedOrder.getCustomerId())
                    .productId(savedOrder.getProductId())
                    .quantity(requestDTO.getQuantity())
                    .totalAmount(savedOrder.getTotalAmount())
                    .build();

            kafkaTemplate.send("order-events", event);

            return mapToResponseDTO(savedOrder);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lưu đơn hàng vào Database: " + e.getMessage());
        }
    }

    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng với id: " + id));
        return mapToResponseDTO(order);
    }

    public ProductDTO getProductFromProductService(Long productId) {
        String targetUrl = "http://product-service/api/v1/products/" + productId;
        
        return restClient.get()
                .uri(targetUrl)
                .retrieve()
                .body(ProductDTO.class);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .productId(order.getProductId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .build();
    }
}
