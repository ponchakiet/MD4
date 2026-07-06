package re.pharmacyservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import re.pharmacyservice.dto.OrderEvent;

import java.sql.Date;

@Service
public class NotificationConsumer {
    @Autowired
    private JavaMailSender mailSender;

    @KafkaListener(topics = "medicine-stock-events", groupId = "notification-group")
    public void handleOrderNotification(OrderEvent event) {
        System.out.println("🔔 [Notification Service]: Đang xử lý hóa đơn...");
        System.out.println(">>> Hóa đơn cho đơn hàng [" + event.getOrderId() + "] đã được gửi tới khách hàng.");

        try {
            sendEmailNotification(event);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email: " + e.getMessage());
        }
    }

    private void sendEmailNotification(OrderEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("customer-email@example.com");
        message.setSubject("Xác nhận đơn hàng thuốc #" + event.getOrderId());
        message.setText("Cảm ơn bạn đã mua thuốc tại hiệu thuốc chúng tôi.\n" +
                "Mã thuốc: " + event.getMedicineId() + "\n" +
                "Số lượng: " + event.getQuantity() + "\n" +
                "Thời gian: " + new Date(event.getTimestamp()));

        System.out.println("📧 Đã gửi email xác nhận thành công!");
    }
}
