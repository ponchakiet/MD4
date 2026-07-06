package re.medicineservice.config;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import re.medicineservice.dto.PharmacyAlert;
import re.medicineservice.service.RedisMessagePublisher;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final RedisMessagePublisher messagePublisher;

    public InventoryController(RedisMessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    // API Giả lập nhập hàng
    @PostMapping("/import")
    public String importMedicine() {
        PharmacyAlert alert = new PharmacyAlert("IMPORT", "Đã nhập 100 hộp Panadol");
        messagePublisher.publish(alert);

        return "Nhập hàng thành công và đã gửi thông báo tới các Dashboard!";
    }
}