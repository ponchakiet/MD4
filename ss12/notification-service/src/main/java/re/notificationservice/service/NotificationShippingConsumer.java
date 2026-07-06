package re.notificationservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import re.notificationservice.dto.ShippingStatusEvent;

@Service
public class NotificationShippingConsumer {
    @KafkaListener(topics = "shipping-events", groupId = "notification-shipping-group")
    public void consumeAndNotify(ShippingStatusEvent event) {
        // Chỉ gửi tin nhắn chúc mừng nếu hàng đã đến tay người nhận thành công
        if ("DELIVERED".equalsIgnoreCase(event.getStatus())) {
            System.out.println("\n📬 [Notification-Service] KÍCH HOẠT GỬI SMS/EMAIL:");
            System.out.println("   Gửi tới hòm thư: " + event.getCustomerEmail());
            System.out.println("   Nội dung: [Hiệu Thuốc] Chúc mừng bạn! Đơn hàng #" + event.getOrderId() + " của bạn đã được giao thành công bởi Shipper.");
            System.out.println("   Cảm ơn bạn đã tin tưởng sử dụng dịch vụ của chúng tôi!");
            System.out.println("=============================================================\n");
        }
    }
}
