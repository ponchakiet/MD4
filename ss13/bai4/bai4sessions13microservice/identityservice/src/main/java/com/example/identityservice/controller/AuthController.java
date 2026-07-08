package com.example.identityservice.controller;

import com.example.identityservice.dto.LoginRequest;
import com.example.identityservice.dto.LoginResponse;
import com.example.identityservice.dto.RegisterRequest;
import com.example.identityservice.dto.RegisterResponse;
import com.example.identityservice.dto.TokenResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.service.UserService;
import com.example.identityservice.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // Bài tập 1: Đăng ký
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            RegisterResponse errorResponse = new RegisterResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Bài tập 3: Đăng nhập - Stateless Authentication
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login attempt for username: {}", loginRequest.getUsername());
            User authenticatedUser = userService.authenticate(loginRequest);
            String token = jwtUtil.generateToken(authenticatedUser);

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .username(authenticatedUser.getUsername())
                    .role(authenticatedUser.getRole())
                    .expiresIn(3600000L)
                    .message("Login successful")
                    .build();

            log.info("Login successful for user: {}", authenticatedUser.getUsername());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.warn("Login failed: {}", e.getMessage());
            LoginResponse errorResponse = LoginResponse.builder()
                    .message("Bad credentials")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // Bài tập 2: Test tạo JWT
    @GetMapping("/test-token")
    public ResponseEntity<TokenResponse> testToken(@RequestParam String username) {
        try {
            log.info("Generating token for username: {}", username);
            User user = userService.findByUsername(username);
            String token = jwtUtil.generateToken(user);

            TokenResponse response = TokenResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .expiresIn(3600000L)
                    .message("Token generated successfully")
                    .build();

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error generating token: {}", e.getMessage());
            TokenResponse errorResponse = TokenResponse.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}