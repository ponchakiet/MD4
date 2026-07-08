package com.example.identityservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    // GET /api/products - Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        log.info("📦 Getting all products from Identity Service");

        List<Map<String, Object>> products = Arrays.asList(
                Map.of("id", 1, "name", "Laptop", "price", 1500.0, "category", "Electronics"),
                Map.of("id", 2, "name", "Phone", "price", 800.0, "category", "Electronics"),
                Map.of("id", 3, "name", "Tablet", "price", 500.0, "category", "Electronics")
        );

        return ResponseEntity.ok(products);
    }

    // GET /api/products/{id} - Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        log.info("📦 Getting product with id: {} from Identity Service", id);

        Map<String, Object> product = Map.of(
                "id", id,
                "name", "Product " + id,
                "price", 100.0 * id,
                "description", "This is product " + id
        );

        return ResponseEntity.ok(product);
    }

    // POST /api/products - Tạo sản phẩm mới
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Map<String, Object> product) {
        log.info("📦 Creating new product in Identity Service: {}", product);

        product.put("id", System.currentTimeMillis());
        product.put("createdAt", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(product);
    }

    // GET /api/products/test - Test endpoint cho Gateway
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testGateway() {
        log.info("🔍 Test endpoint called via Gateway");

        // Lấy headers để kiểm tra
        Map<String, String> response = Map.of(
                "message", "Gateway routing is working! ✅",
                "service", "Identity Service",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "status", "success"
        );

        return ResponseEntity.ok(response);
    }

    // GET /api/products/headers - Kiểm tra headers từ Gateway
    @GetMapping("/headers")
    public ResponseEntity<Map<String, String>> testHeaders(@RequestHeader Map<String, String> headers) {
        log.info("📨 Headers from Gateway: {}", headers);

        Map<String, String> response = Map.of(
                "x-gateway-request", headers.getOrDefault("x-gateway-request", "not-present"),
                "x-service-name", headers.getOrDefault("x-service-name", "not-present"),
                "x-gateway-service", headers.getOrDefault("x-gateway-service", "not-present"),
                "message", "Headers received successfully"
        );

        return ResponseEntity.ok(response);
    }

    // PUT /api/products/{id} - Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> product) {
        log.info("📦 Updating product with id: {}", id);

        product.put("id", id);
        product.put("updatedAt", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(product);
    }

    // DELETE /api/products/{id} - Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        log.info("📦 Deleting product with id: {}", id);

        return ResponseEntity.ok(Map.of(
                "message", "Product deleted successfully",
                "id", id.toString()
        ));
    }
}