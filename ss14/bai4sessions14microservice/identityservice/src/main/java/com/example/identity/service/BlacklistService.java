package com.example.identity.service;

import com.example.identity.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BlacklistService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void addToBlacklist(String token) {
        try {
            // 1. Giải mã token để lấy claim "jti" và "exp"
            String jti = jwtUtil.extractJti(token);
            Date expiration = jwtUtil.extractExpiration(token);

            if (jti == null) {
                throw new IllegalArgumentException("Token không chứa JTI");
            }

            // 2. Tính toán TTL (Time-To-Live)
            long now = System.currentTimeMillis();
            long expTime = expiration.getTime();
            long ttlMilliseconds = expTime - now;
            long ttlSeconds = ttlMilliseconds / 1000;

            // Nếu token đã hết hạn, không cần blacklist
            if (ttlSeconds <= 0) {
                log.info("Token đã hết hạn, không cần thêm vào blacklist");
                return;
            }

            // 3. Lưu key "blacklist:{jti}" vào Redis với giá trị "revoked"
            String redisKey = BLACKLIST_PREFIX + jti;
            redisTemplate.opsForValue().set(redisKey, "revoked");

            // 4. Thiết lập thời gian tự động xóa (Expiration)
            redisTemplate.expire(redisKey, ttlSeconds, TimeUnit.SECONDS);

            log.info("Đã thêm token vào blacklist với JTI: {}, TTL: {} seconds", jti, ttlSeconds);

        } catch (Exception e) {
            log.error("Lỗi khi thêm token vào blacklist: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi thêm token vào blacklist: " + e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            String jti = jwtUtil.extractJti(token);
            if (jti == null) {
                return false;
            }

            String redisKey = BLACKLIST_PREFIX + jti;
            Boolean exists = redisTemplate.hasKey(redisKey);

            if (Boolean.TRUE.equals(exists)) {
                log.debug("Token đã bị blacklist: {}", jti);
            }

            return Boolean.TRUE.equals(exists);

        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra blacklist: {}", e.getMessage());
            return false;
        }
    }

    public void removeFromBlacklist(String jti) {
        String redisKey = BLACKLIST_PREFIX + jti;
        redisTemplate.delete(redisKey);
        log.info("Đã xóa token khỏi blacklist: {}", jti);
    }
}