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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestClient restClient;

    public OrderService(OrderRepository orderRepository, RestClient.Builder restClientBuilder) {
        this.orderRepository = orderRepository;
        this.restClient = restClientBuilder.build();
    }

    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        ProductDTO product = getProductFromProductService(requestDTO.getProductId());
        BigDecimal productPrice = product.getPrice();
        
        BigDecimal totalAmount = productPrice.multiply(BigDecimal.valueOf(requestDTO.getQuantity()));

        Order order = Order.builder()
                .customerId(requestDTO.getCustomerId())
                .productId(requestDTO.getProductId())
                .orderDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .build();

        try {
            Order savedOrder = orderRepository.save(order);
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
                .build();
    }
}
