package com.example.identity.repository;

import com.example.identity.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByUserId(UUID userId);
    void deleteAllByExpiryDateBefore(Instant expiryDate);
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
}