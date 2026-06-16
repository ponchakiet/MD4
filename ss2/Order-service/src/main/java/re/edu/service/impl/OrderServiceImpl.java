package re.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import re.edu.dto.request.OrderRequestDTO;
import re.edu.dto.response.OrderResponseDTO;
import re.edu.entity.Order;
import re.edu.exception.ResourceNotFoundException;
import re.edu.repository.IOrderRepository;
import re.edu.service.IOrderService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final IOrderRepository orderRepository;

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Double coursePrice = 500000.0;

        Order order = new Order();
        order.setStudentId(request.getStudentId());
        order.setCourseId(request.getCourseId());
        order.setTotalAmount(coursePrice);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        return OrderResponseDTO.builder()
                .message("Tạo đơn hàng thành công")
                .orderId(savedOrder.getId())
                .totalAmount(savedOrder.getTotalAmount())
                .build();
    }
}
