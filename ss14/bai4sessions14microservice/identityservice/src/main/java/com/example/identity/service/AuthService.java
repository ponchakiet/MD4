package com.example.identity.service;

import com.example.identity.dto.AuthRequest;
import com.example.identity.dto.AuthResponse;
import com.example.identity.entity.RefreshToken;
import com.example.identity.entity.User;
import com.example.identity.repository.RefreshTokenRepository;
import com.example.identity.repository.UserRepository;
import com.example.identity.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse login(AuthRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());

        // Save refresh token to database
        saveRefreshToken(refreshToken, request.getUsername());

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtUtil.extractExpiration(accessToken).getTime()
        );
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token không hợp lệ");
        }

        String username = jwtUtil.extractUsername(refreshToken);

        // Check if refresh token exists and not revoked
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại"));

        if (storedToken.isRevoked()) {
            throw new RuntimeException("Refresh token đã bị thu hồi");
        }

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token đã hết hạn");
        }

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(username);

        return new AuthResponse(
                newAccessToken,
                refreshToken,
                "Bearer",
                jwtUtil.extractExpiration(newAccessToken).getTime()
        );
    }

    @Transactional
    public void logout(String token) {
        try {
            // 1. Validate token
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Token không hợp lệ");
            }

            // 2. Add token to blacklist
            blacklistService.addToBlacklist(token);

            // 3. Delete refresh token
            String username = jwtUtil.extractUsername(token);
            refreshTokenRepository.deleteByUsername(username);

            log.info("User {} đã đăng xuất thành công", username);

        } catch (Exception e) {
            log.error("Lỗi khi đăng xuất: {}", e.getMessage());
            throw new RuntimeException("Đăng xuất thất bại: " + e.getMessage());
        }
    }

    @Transactional
    public void register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        userRepository.save(user);
        log.info("Đã đăng ký user mới: {}", request.getUsername());
    }

    private void saveRefreshToken(String token, String username) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(86400)); // 24 hours
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
    }
}