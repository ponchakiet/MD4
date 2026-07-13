package com.example.identity.controller;

import com.example.identity.dto.ApiResponse;
import com.example.identity.dto.AuthRequest;
import com.example.identity.dto.AuthResponse;
import com.example.identity.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody AuthRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok(new ApiResponse(true, "Đăng ký thành công"));
        } catch (Exception e) {
            log.error("Lỗi đăng ký: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Đăng ký thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse(true, "Đăng nhập thành công", response));
        } catch (Exception e) {
            log.error("Lỗi đăng nhập: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "Đăng nhập thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new RuntimeException("Refresh token không hợp lệ");
            }

            String refreshToken = authorization.substring(7);
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(new ApiResponse(true, "Refresh token thành công", response));
        } catch (Exception e) {
            log.error("Lỗi refresh token: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "Refresh token thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new RuntimeException("Token không hợp lệ");
            }

            String token = authorization.substring(7);
            authService.logout(token);

            return ResponseEntity.ok(new ApiResponse(true, "Đăng xuất thành công"));
        } catch (Exception e) {
            log.error("Lỗi đăng xuất: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "Đăng xuất thất bại: " + e.getMessage()));
        }
    }
}