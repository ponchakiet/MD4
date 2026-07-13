package com.example.product.controller;

import com.example.product.dto.ProductResponse;
import com.example.product.model.Product;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products - Cho phép mọi user đã đăng nhập truy cập
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        // Lấy thông tin user từ SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("User {} is accessing all products", auth.getName());

        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id} - Cho phép mọi user đã đăng nhập truy cập
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("User {} is accessing product with id: {}", auth.getName(), id);

        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/products/{id} - Chỉ cho phép ADMIN thực hiện
     * Sử dụng @PreAuthorize để kiểm tra role
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được xóa
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("ADMIN user {} is deleting product with id: {}", auth.getName(), id);

        boolean deleted = productService.deleteProduct(id);

        if (deleted) {
            return ResponseEntity.ok(ProductResponse.builder()
                    .message("Product with id " + id + " has been deleted successfully")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ProductResponse.builder()
                            .message("Product with id " + id + " not found")
                            .build());
        }
    }

    /**
     * POST /api/products - Chỉ cho phép ADMIN thực hiện
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("ADMIN user {} is creating new product", auth.getName());

        Product created = productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/products/{id} - Chỉ cho phép ADMIN thực hiện
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("ADMIN user {} is updating product with id: {}", auth.getName(), id);

        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}