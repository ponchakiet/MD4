package com.example.identityservice.dto;

public class TokenResponse {
    private String token;
    private String username;
    private String role;
    private Long expiresIn;
    private String message;

    public TokenResponse() {}

    public TokenResponse(String token, String username, String role, Long expiresIn, String message) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresIn = expiresIn;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String username;
        private String role;
        private Long expiresIn;
        private String message;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public TokenResponse build() {
            return new TokenResponse(token, username, role, expiresIn, message);
        }
    }
}