package com.example.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
        System.out.println("🚀 Identity Service started successfully!");
        System.out.println("📌 API endpoints:");
        System.out.println("   POST /api/auth/register - Đăng ký");
        System.out.println("   POST /api/auth/login - Đăng nhập");
        System.out.println("   POST /api/auth/refresh - Refresh token");
        System.out.println("   POST /api/auth/logout - Đăng xuất");
        System.out.println("   GET  /api/test/hello - Test authentication");
    }
}