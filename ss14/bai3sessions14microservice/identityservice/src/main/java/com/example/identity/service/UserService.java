package com.example.identity.service;

import com.example.identity.dto.request.RegisterRequest;
import com.example.identity.dto.response.UserResponseDTO;
import com.example.identity.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    UserResponseDTO createUser(RegisterRequest request);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}