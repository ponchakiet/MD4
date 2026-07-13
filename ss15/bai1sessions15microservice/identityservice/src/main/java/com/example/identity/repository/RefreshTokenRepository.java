package com.example.identity.repository;

import com.example.identity.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.username = :username")
    void deleteByUsername(@Param("username") String username);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Transactional
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.username = :username")
    void revokeAllByUsername(@Param("username") String username);

    boolean existsByToken(String token);
}