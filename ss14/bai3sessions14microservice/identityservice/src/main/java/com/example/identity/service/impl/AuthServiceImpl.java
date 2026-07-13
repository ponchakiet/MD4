package com.example.identity.service.impl;

import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.request.RegisterRequest;
import com.example.identity.dto.response.TokenResponseDTO;
import com.example.identity.dto.response.UserResponseDTO;
import com.example.identity.entity.RefreshToken;
import com.example.identity.entity.User;
import com.example.identity.exception.TokenExpiredException;
import com.example.identity.exception.UnauthorizedException;
import com.example.identity.repository.RefreshTokenRepository;
import com.example.identity.security.JwtUtils;
import com.example.identity.service.AuthService;
import com.example.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TokenResponseDTO login(LoginRequest request) {
        log.info("Login request for user: {}", request.getUsername());

        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!user.getIsActive()) {
            throw new UnauthorizedException("User account is deactivated");
        }

        String accessToken = jwtUtils.generateAccessToken(user.getId().toString());
        RefreshToken refreshToken = createRefreshToken(user.getId());

        log.info("Login successful for user: {}", user.getUsername());

        TokenResponseDTO response = new TokenResponseDTO();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setTokenType("Bearer");
        response.setExpiresIn(900L);

        return response;
    }

    @Override
    @Transactional
    public TokenResponseDTO refreshToken(String requestToken) {
        log.info("Processing refresh token request");

        // Bước 1: Tìm RefreshToken trong DB
        RefreshToken oldRefreshToken = refreshTokenRepository
                .findByToken(requestToken)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found: {}", requestToken);
                    return new TokenExpiredException("Refresh token not found or invalid");
                });

        // Bước 2: Kiểm tra token đã bị thu hồi
        if (oldRefreshToken.getIsRevoked()) {
            log.warn("Refresh token has been revoked: {}", requestToken);
            throw new TokenExpiredException("Refresh token has been revoked");
        }

        // Bước 3: Kiểm tra token đã hết hạn
        if (oldRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Refresh token expired: {}", requestToken);
            refreshTokenRepository.delete(oldRefreshToken);
            throw new TokenExpiredException("Refresh token expired");
        }

        // Bước 4: (QUAN TRỌNG - Rotation) Xóa Refresh Token cũ
        refreshTokenRepository.delete(oldRefreshToken);
        log.info("Deleted old refresh token: {}", requestToken);

        // Bước 5: Lấy User từ token cũ
        Long userId = oldRefreshToken.getUserId();
        User user = userService.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Bước 6: Tạo Access Token mới
        String newAccessToken = jwtUtils.generateAccessToken(user.getId().toString());

        // Bước 7: Tạo Refresh Token hoàn toàn mới
        RefreshToken newRefreshToken = createRefreshToken(userId);
        String newRefreshTokenString = newRefreshToken.getToken();

        log.info("Token rotation completed for user: {}", user.getUsername());

        TokenResponseDTO response = new TokenResponseDTO();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshTokenString);
        response.setTokenType("Bearer");
        response.setExpiresIn(900L);

        return response;
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("Logout request");
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.setIsRevoked(true);
                    refreshTokenRepository.save(token);
                    log.info("Token revoked: {}", refreshToken);
                });
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        log.info("Revoking all tokens for user: {}", userId);
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public UserResponseDTO register(RegisterRequest request) {
        log.info("Register request for user: {}", request.getUsername());

        if (userService.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        return userService.createUser(request);
    }

    private RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserId(userId);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshToken.setIsRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }
}