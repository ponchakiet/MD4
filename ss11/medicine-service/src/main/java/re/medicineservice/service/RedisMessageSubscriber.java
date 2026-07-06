package re.medicineservice.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import re.medicineservice.dto.PharmacyAlert;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class RedisMessageSubscriber implements MessageListener {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String jsonBody = new String(message.getBody());

            PharmacyAlert alert = objectMapper.readValue(jsonBody, PharmacyAlert.class);

            System.out.println("\n=== [DASHBOARD NOTIFICATION] ===");
            System.out.println("Loại thông báo: " + alert.getType());
            System.out.println("Nội dung: " + alert.getMessage());
            System.out.println("================================\n");

        } catch (Exception e) {
            System.err.println("Lỗi phân rã JSON từ Redis Message: " + e.getMessage());
        }
    }
}
