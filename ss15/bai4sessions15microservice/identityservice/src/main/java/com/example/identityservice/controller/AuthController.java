package com.example.identityservice.controller;

import com.example.identityservice.dto.AuthRequest;
import com.example.identityservice.dto.AuthResponse;
import com.example.identityservice.dto.RefreshRequest;
import com.example.identityservice.entity.RefreshToken;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.TokenExpiredException;
import com.example.identityservice.exception.TokenNotFoundException;
import com.example.identityservice.exception.TokenRevokedException;
import com.example.identityservice.service.JwtService;
import com.example.identityservice.service.RefreshTokenService;
import com.example.identityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userService.findByUsername(request.getUsername());
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                user.getRole(),
                user.getUsername()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest request) {
        try {
            RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(request.getRefreshToken());
            User user = newRefreshToken.getUser();
            String newAccessToken = jwtService.generateAccessToken(user);

            return ResponseEntity.ok(new AuthResponse(
                    newAccessToken,
                    newRefreshToken.getToken(),
                    user.getRole(),
                    user.getUsername()
            ));

        } catch (TokenNotFoundException | TokenRevokedException | TokenExpiredException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "Forbidden",
                            "message", e.getMessage()
                    ));
        }
    }
}