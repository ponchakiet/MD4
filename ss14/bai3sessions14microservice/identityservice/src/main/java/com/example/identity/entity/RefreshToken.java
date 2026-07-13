package com.example.identity.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "is_revoked")
    private Boolean isRevoked = false;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;
}