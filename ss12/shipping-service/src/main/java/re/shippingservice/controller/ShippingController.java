package re.shippingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import re.shippingservice.service.ShippingService;

@RestController
@RequestMapping("/api/v1/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateStatus(@RequestParam String orderId,
                                               @RequestParam String status,
                                               @RequestParam String email) {
        shippingService.updateShippingStatus(orderId, status, email);
        return ResponseEntity.ok("Đã cập nhật trạng thái giao hàng thành công!");
    }
}