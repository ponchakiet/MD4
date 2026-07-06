package re.promoteservice.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import re.productservice.service.ProductService;
import re.promoteservice.dto.PromotionMessage;
import tools.jackson.databind.ObjectMapper;

@Service
public class RedisMessageSubscriber implements MessageListener {

    private final ProductService productService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisMessageSubscriber(@Lazy ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 1. Đọc chuỗi JSON từ body tin nhắn
            String jsonBody = new String(message.getBody());

            // 2. Chuyển đổi thành Object PromotionMessage
            PromotionMessage promotionMessage = objectMapper.readValue(jsonBody, PromotionMessage.class);
            Long productId = promotionMessage.getProductId();

            System.out.println("\n📣 [Product-Service] Nhận tín hiệu khuyến mãi từ Redis Channel!");
            System.out.println("--> Tiến hành xử lý xóa cache khẩn cấp cho sản phẩm ID: " + productId);

            // 3. Gọi hàm xóa cache chủ động tại Service
            productService.clearProductCache(productId);

        } catch (Exception e) {
            System.err.println("Lỗi xử lý tin nhắn từ Pub/Sub: " + e.getMessage());
        }
    }
}