package com.example.identity.controller;

import com.example.identity.dto.AuthenticationResponse;
import com.example.identity.dto.LoginRequest;
import com.example.identity.entity.RefreshToken;
import com.example.identity.entity.User;
import com.example.identity.service.RefreshTokenService;
import com.example.identity.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 1. Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 2. Get user details
            User user = (User) authentication.getPrincipal();

            // 3. Generate Access Token
            String accessToken = jwtUtils.generateAccessToken(user);

            // 4. Generate Refresh Token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            // 5. Build response
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .success(true)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtUtils.extractExpiration(accessToken).getEpochSecond())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .message("Login successful")
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(AuthenticationResponse.builder()
                            .success(false)
                            .message("Invalid username or password")
                            .build());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody String refreshToken) {
        try {
            // 1. Validate refresh token from DB
            RefreshToken token = refreshTokenService.findByToken(refreshToken);
            refreshTokenService.verifyExpiration(token);

            // 2. Get user
            User user = token.getUser();

            // 3. Generate new access token
            String newAccessToken = jwtUtils.generateAccessToken(user);

            // 4. Build response
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .success(true)
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtils.extractExpiration(newAccessToken).getEpochSecond())
                    .username(user.getUsername())
                    .message("Token refreshed successfully")
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(AuthenticationResponse.builder()
                            .success(false)
                            .message("Invalid refresh token: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logout(@RequestBody String refreshToken) {
        try {
            refreshTokenService.revokeToken(refreshToken);
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .success(true)
                    .message("Logout successful")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(AuthenticationResponse.builder()
                            .success(false)
                            .message("Logout failed: " + e.getMessage())
                            .build());
        }
    }
}