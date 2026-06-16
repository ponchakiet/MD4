package re.edu.service;

import re.edu.dto.request.OrderRequestDTO;
import re.edu.dto.response.OrderResponseDTO;
import re.edu.entity.Order;

public interface IOrderService {
    Order getOrderById(Long id);
    OrderResponseDTO createOrder(OrderRequestDTO request);
}
