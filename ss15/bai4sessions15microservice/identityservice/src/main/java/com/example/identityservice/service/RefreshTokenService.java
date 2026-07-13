package com.example.identityservice.service;

import com.example.identityservice.entity.RefreshToken;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.TokenExpiredException;
import com.example.identityservice.exception.TokenNotFoundException;
import com.example.identityservice.exception.TokenRevokedException;
import com.example.identityservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        String token = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String refreshToken) {
        log.info("Rotating refresh token");

        RefreshToken existingToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token not found"));

        if (existingToken.isRevoked()) {
            throw new TokenRevokedException("Refresh token has been revoked");
        }

        if (existingToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Refresh token has expired");
        }

        User user = existingToken.getUser();

        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);
        log.info("Revoked old refresh token");

        return createRefreshToken(user);
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}