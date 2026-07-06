package re.shippingservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import re.shippingservice.dto.ShippingStatusEvent;

import java.util.UUID;

@Service
public class ShippingService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "shipping-events";

    public ShippingService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void updateShippingStatus(String orderId, String status, String email) {
        String shippingId = "SHIP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Đóng gói thông tin sự kiện vận chuyển
        ShippingStatusEvent event = new ShippingStatusEvent(orderId, shippingId, status, email);

        // Phát sự kiện lên Kafka
        kafkaTemplate.send(TOPIC, orderId, event);
        System.out.println("==> [Shipping-Service] Shipper cập nhật trạng thái [" + status + "] cho đơn hàng: " + orderId);
    }
}
