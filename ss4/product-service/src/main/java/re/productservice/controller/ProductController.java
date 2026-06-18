package re.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.productservice.dto.request.ProductRequest;
import re.productservice.entity.Product;
import re.productservice.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        Product savedProduct = productService.createProduct(request);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Product> getProductById(@RequestParam Long id) {
        Product savedProduct = productService.getProductById(id);
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
    }
}
