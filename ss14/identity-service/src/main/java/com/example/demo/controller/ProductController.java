package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    public List<String> getProducts() {
        return Arrays.asList("Product 1", "Product 2", "Product 3");
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestHeader(value = "X-User-Role", required = false) String role) {
        if ("ADMIN".equals(role)) {
            return ResponseEntity.ok("Product created successfully");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
    }
}
