package com.example.identity.service;

import com.example.identity.config.JwtProperties;
import com.example.identity.entity.RefreshToken;
import com.example.identity.entity.User;
import com.example.identity.exception.TokenRefreshException;
import com.example.identity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Delete existing refresh tokens for this user
        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration()))
                .user(user)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isRevoked()) {
            throw new TokenRefreshException("Refresh token has been revoked");
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token has expired. Please login again.");
        }

        return token;
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found or revoked"));
    }

    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = findByToken(token);
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteExpiredTokens() {
        Instant now = Instant.now();
        int deletedCount = refreshTokenRepository.deleteAllByExpiryDateBefore(now);
        log.info("Deleted {} expired refresh tokens", deletedCount);
    }
}