package re.promoteservice.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import re.promoteservice.dto.PromotionMessage;
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

    public void publishPromotionUpdate(Long productId) {
        try {
            PromotionMessage message = new PromotionMessage(productId);
            // Đóng gói Object thành chuỗi JSON String
            String jsonMessage = objectMapper.writeValueAsString(message);

            // Bắn tin nhắn vào kênh "promotion-updates"
            redisTemplate.convertAndSend(topic.getTopic(), jsonMessage);
            System.out.println("==> [Promotion-Service] Đã phát thông báo cập nhật giá cho sản phẩm ID: " + productId);
        } catch (Exception e) {
            System.err.println("Lỗi phát tin nhắn khuyến mãi: " + e.getMessage());
        }
    }
}