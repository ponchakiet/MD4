package com.example.identity.controller;

import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.request.RefreshTokenRequest;
import com.example.identity.dto.request.RegisterRequest;
import com.example.identity.dto.response.TokenResponseDTO;
import com.example.identity.dto.response.UserResponseDTO;
import com.example.identity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for user: {}", request.getUsername());
        TokenResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request received");
        TokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        log.info("Logout request received");
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received for user: {}", request.getUsername());
        UserResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tokens/{userId}")
    public ResponseEntity<Void> revokeUserTokens(@PathVariable Long userId) {
        log.info("Revoke tokens request for user: {}", userId);
        authService.revokeAllUserTokens(userId);
        return ResponseEntity.ok().build();
    }
}