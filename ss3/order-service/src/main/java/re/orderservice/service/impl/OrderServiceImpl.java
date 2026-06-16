package re.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import re.orderservice.dto.request.OrderRequestDTO;
import re.orderservice.entity.Order;
import re.orderservice.repository.IOrderRepository;
import re.orderservice.service.IOrderService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final IOrderRepository orderRepository;

    @Override
    public Order createOrder(OrderRequestDTO dto) {
        try {
            // Giả lập lấy giá từ Product Service (Ví dụ: 100.0)
            double productPrice = 100.0;

            Order order = new Order();
            order.setCustomerId(dto.getCustomerId());
            order.setProductId(dto.getProductId());
            order.setOrderDate(LocalDateTime.now());
            order.setTotalAmount(productPrice * dto.getQuantity());

            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save order to database");
        }
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }
}
