package com.example.product.service;

import com.example.product.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class ProductService {

    private final List<Product> products = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public ProductService() {
        // Khởi tạo dữ liệu giả
        initProducts();
    }

    private void initProducts() {
        products.add(Product.builder()
                .id(idCounter.getAndIncrement())
                .name("Laptop Dell XPS 13")
                .description("Intel Core i7, 16GB RAM, 512GB SSD")
                .price(new BigDecimal("1499.99"))
                .stockQuantity(10)
                .category("Electronics")
                .build());

        products.add(Product.builder()
                .id(idCounter.getAndIncrement())
                .name("iPhone 15 Pro Max")
                .description("6.7-inch display, 256GB storage")
                .price(new BigDecimal("1199.99"))
                .stockQuantity(15)
                .category("Electronics")
                .build());

        products.add(Product.builder()
                .id(idCounter.getAndIncrement())
                .name("Samsung Galaxy S24 Ultra")
                .description("6.8-inch display, 512GB storage")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(8)
                .category("Electronics")
                .build());

        products.add(Product.builder()
                .id(idCounter.getAndIncrement())
                .name("Nike Air Max 270")
                .description("Comfortable running shoes")
                .price(new BigDecimal("149.99"))
                .stockQuantity(30)
                .category("Fashion")
                .build());

        products.add(Product.builder()
                .id(idCounter.getAndIncrement())
                .name("Sony WH-1000XM5")
                .description("Wireless noise-canceling headphones")
                .price(new BigDecimal("399.99"))
                .stockQuantity(20)
                .category("Electronics")
                .build());

        log.info("Initialized {} products", products.size());
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Optional<Product> getProductById(Long id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    public boolean deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        return products.removeIf(product -> product.getId().equals(id));
    }

    public Product addProduct(Product product) {
        product.setId(idCounter.getAndIncrement());
        products.add(product);
        return product;
    }

    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return getProductById(id).map(existingProduct -> {
            existingProduct.setName(productDetails.getName());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setStockQuantity(productDetails.getStockQuantity());
            existingProduct.setCategory(productDetails.getCategory());
            return existingProduct;
        });
    }
}