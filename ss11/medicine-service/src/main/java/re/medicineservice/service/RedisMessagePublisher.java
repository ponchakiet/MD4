package re.medicineservice.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import re.medicineservice.dto.PharmacyAlert;
import tools.jackson.databind.ObjectMapper;

@Service
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(PharmacyAlert alert) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(alert);

            // Bắn tin nhắn vào channel "pharmacy-alerts"
            redisTemplate.convertAndSend(topic.getTopic(), jsonMessage);
            System.out.println("==> [Publisher] Đã phát thông báo nhập hàng thành công.");

        } catch (Exception e) {
            System.err.println("Lỗi phát tin nhắn: " + e.getMessage());
        }
    }
}
