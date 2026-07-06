package re.pharmacyservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import re.pharmacyservice.dto.OrderEvent;
import re.pharmacyservice.service.PharmacyService;

import java.util.UUID;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {
    @Autowired
    private PharmacyService producer;

    @PostMapping("/checkout")
    public String checkout(@RequestBody OrderEvent request) {
        request.setOrderId(UUID.randomUUID().toString());

        producer.sendOrder(request);

        return "Thanh toán thành công cho đơn hàng: " + request.getOrderId();
    }
}
