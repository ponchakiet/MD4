package com.example.identityservice.dto;

import java.util.UUID;

public class RegisterResponse {
    private UUID id;
    private String username;
    private String role;
    private String message;

    public RegisterResponse() {}

    public RegisterResponse(UUID id, String username, String role, String message) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.message = message;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String username;
        private String role;
        private String message;

        public Builder id(UUID id) {
            this.id = id;
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

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public RegisterResponse build() {
            return new RegisterResponse(id, username, role, message);
        }
    }
}