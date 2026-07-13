package com.ecommerce.product.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PromotionMessageListener implements MessageListener {

    private final CacheManager cacheManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        log.info("Received promotion update message for product id: {}", body);
        
        try {
            Long productId = Long.valueOf(body);
            var cache = cacheManager.getCache("products");
            if (cache != null) {
                cache.evict(productId);
                log.info("Successfully evicted product {} from cache.", productId);
            }
        } catch (NumberFormatException e) {
            log.error("Failed to parse product ID from message: {}", body, e);
        }
    }
}
