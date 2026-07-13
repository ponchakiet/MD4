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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse login(AuthRequest request) {
        log.info("Processing login for user: {}", request.getUsername());

        try {
            // 1. Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Get user details
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 3. Get user role (default to STUDENT if not set)
            String role = user.getRoles().isEmpty() ? "STUDENT" : user.getRoles().iterator().next();

            // 4. Generate Access Token (15 minutes)
            String accessToken = jwtUtil.generateAccessToken(user.getUsername(), role);

            // 5. Generate Refresh Token (7 days)
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            // 6. Save Refresh Token to database
            saveRefreshToken(refreshToken, user.getUsername());

            // 7. Build response
            AuthResponse response = new AuthResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(jwtUtil.extractExpiration(accessToken).getTime());
            response.setUsername(user.getUsername());
            response.setRole(role);

            log.info("User {} logged in successfully", user.getUsername());
            return response;

        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    private void saveRefreshToken(String token, String username) {
        // Revoke old refresh tokens
        refreshTokenRepository.revokeAllByUsername(username);

        // Create new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(604800)); // 7 days
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
        log.info("Saved refresh token for user: {}", username);
    }

    @Transactional
    public void createDefaultUser() {
        String username = "testuser";

        if (userRepository.existsByUsername(username)) {
            log.info("Default user already exists");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEmail("testuser@example.com");
        user.setFullName("Test User");
        user.setActive(true);

        HashSet<String> roles = new HashSet<>();
        roles.add("STUDENT");
        user.setRoles(roles);

        userRepository.save(user);
        log.info("Created default user: {}", username);
    }
}