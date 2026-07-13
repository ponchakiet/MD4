package com.example.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
}