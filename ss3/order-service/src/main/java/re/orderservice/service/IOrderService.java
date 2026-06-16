package re.orderservice.service;

import re.orderservice.dto.request.OrderRequestDTO;
import re.orderservice.entity.Order;

public interface IOrderService {
    Order createOrder(OrderRequestDTO dto);
    Order getOrder(Long id);
}
