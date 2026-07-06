package re.emailservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import re.emailservice.dto.OrderCreatedEvent;

@Service
public class EmailConsumer {

    @KafkaListener(topics = "order-events", groupId = "email-group")
    public void sendEmailAlert(OrderCreatedEvent event) {
        System.out.println("\n📬 [Email-Service] Kích hoạt tiến trình gửi Email tự động!");
        System.out.println("--------------------------------------------------");
        System.out.println("To: " + event.getCustomerEmail());
        System.out.println("Subject: [Hiệu Thuốc] Xác nhận tiếp nhận đơn hàng thành công #" + event.getOrderId());
        System.out.println("Nội dung chi tiết:");
        System.out.println("   Xin chào, hệ thống đã ghi nhận bạn đặt mua Sản phẩm ID: " + event.getProductId());
        System.out.println("   Số lượng: " + event.getQuantity() + " sản phẩm.");
        System.out.println("   Tổng chi phí thanh toán dự kiến: " + event.getTotalPrice() + " VND.");
        System.out.println("Đơn hàng của bạn đang được đóng gói và giao đi.");
        System.out.println("=============================================\n");
    }
}