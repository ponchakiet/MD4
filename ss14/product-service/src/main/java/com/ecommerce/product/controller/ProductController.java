package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequestDTO;
import com.ecommerce.product.dto.ProductResponseDTO;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO response = productService.createProduct(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO response = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/decrement-stock")
    public ResponseEntity<Void> decrementStock(@PathVariable Long id, @RequestParam Integer quantity) {
        productService.decrementStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/flash-sale/start/{productId}")
    public ResponseEntity<String> startFlashSale(@PathVariable Long productId, @RequestParam Integer stock) {
        productService.startFlashSale(productId, stock);
        return ResponseEntity.ok("Flash sale started for product ID: " + productId + " with stock: " + stock);
    }

    @PostMapping("/flash-sale/buy")
    public ResponseEntity<String> buyFlashSale(@Valid @RequestBody com.ecommerce.product.dto.FlashSaleOrderRequestDTO requestDTO) {
        productService.buyFlashSale(requestDTO);
        return ResponseEntity.ok("Flash sale order processing requested.");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> responses = productService.getAllProducts();
        return ResponseEntity.ok(responses);
    }
}
