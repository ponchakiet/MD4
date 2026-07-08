package com.example.identityservice.util;

import com.example.identityservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("username", user.getUsername());

        return Jwts.builder()
                .claims(claims)  // Thay đổi: setClaims -> claims
                .subject(user.getUsername())  // Thay đổi: setSubject -> subject
                .issuedAt(new Date())  // Thay đổi: setIssuedAt -> issuedAt
                .expiration(new Date(System.currentTimeMillis() + expirationTime))  // Thay đổi: setExpiration -> expiration
                .signWith(getSigningKey())  // Thay đổi: signWith không cần algorithm
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())  // Thay đổi: parserBuilder -> parser, setSigningKey -> verifyWith
                .build()
                .parseSignedClaims(token)  // Thay đổi: parseClaimsJws -> parseSignedClaims
                .getPayload();  // Thay đổi: getBody -> getPayload
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}