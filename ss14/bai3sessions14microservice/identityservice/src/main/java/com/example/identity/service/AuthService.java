package com.example.identity.service;

import com.example.identity.dto.request.LoginRequest;
import com.example.identity.dto.request.RegisterRequest;
import com.example.identity.dto.response.TokenResponseDTO;
import com.example.identity.dto.response.UserResponseDTO;

public interface AuthService {
    TokenResponseDTO login(LoginRequest request);
    TokenResponseDTO refreshToken(String refreshToken);
    void logout(String refreshToken);
    void revokeAllUserTokens(Long userId);
    UserResponseDTO register(RegisterRequest request);
}