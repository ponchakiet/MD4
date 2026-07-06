package re.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import re.orderservice.entity.Order;
import re.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> create(@RequestParam String email,
                                         @RequestParam Long productId,
                                         @RequestParam Integer quantity,
                                         @RequestParam Double price) {
        Order order = orderService.placeOrder(email, productId, quantity, price);
        return ResponseEntity.ok("Đặt hàng thành công! Đơn hàng của bạn đang được xử lý ngầm. Mã đơn: " + order.getId());
    }

    @PostMapping
    public String checkout(@RequestParam String email,
                           @RequestParam Long productId,
                           @RequestParam int quantity,
                           @RequestParam double price) {
        String orderId = orderService.createOrder(email, productId, quantity, price);
        return "Đặt hàng thành công! Mã đơn hàng: " + orderId;
    }
}