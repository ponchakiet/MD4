package com.example.identity;

import com.example.identity.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IdentityServiceApplication {

    @Autowired
    private AuthService authService;

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
        System.out.println("==========================================");
        System.out.println("🚀 Identity Service started successfully!");
        System.out.println("📌 API Endpoints:");
        System.out.println("   POST /api/auth/login  - Login");
        System.out.println("   POST /api/auth/register - Register");
        System.out.println("   GET  /api/auth/health - Health check");
        System.out.println("==========================================");
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Tạo user mặc định khi ứng dụng khởi động
            authService.createDefaultUser();
        };
    }
}