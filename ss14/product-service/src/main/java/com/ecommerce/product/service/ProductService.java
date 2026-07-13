package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequestDTO;
import com.ecommerce.product.dto.ProductResponseDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import com.ecommerce.product.dto.FlashSaleOrderRequestDTO;
import com.ecommerce.product.event.FlashSaleOrderEvent;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void startFlashSale(Long productId, Integer stock) {
        // Initialize stock on Redis using key: flash-sale:product:{id}:stock
        stringRedisTemplate.opsForValue().set("flash-sale:product:" + productId + ":stock", String.valueOf(stock));
    }

    public void buyFlashSale(FlashSaleOrderRequestDTO requestDTO) {
        Long productId = requestDTO.getProductId();
        RLock lock = redissonClient.getLock("lock:flash-sale:product:" + productId);
        boolean isDeducted = false;
        
        try {
            boolean isLocked = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not acquire flash sale lock");
            }

            String stockKey = "flash-sale:product:" + productId + ":stock";
            String stockVal = stringRedisTemplate.opsForValue().get(stockKey);
            
            if (stockVal == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm không trong chương trình Flash Sale");
            }
            
            int currentStock = Integer.parseInt(stockVal);
            if (currentStock < requestDTO.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm đã hết hàng");
            }
            
            // Decrement stock in Redis
            stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(currentStock - requestDTO.getQuantity()));
            isDeducted = true;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lock acquisition interrupted");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        if (isDeducted) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
            
            BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(requestDTO.getQuantity()));
            
            FlashSaleOrderEvent event = FlashSaleOrderEvent.builder()
                    .productId(productId)
                    .customerId(requestDTO.getCustomerId())
                    .quantity(requestDTO.getQuantity())
                    .totalAmount(totalAmount)
                    .build();
            
            kafkaTemplate.send("flash-sale-events", event);
        }
    }

    @Transactional
    public void decrementStock(Long id, Integer quantity) {
        RLock lock = redissonClient.getLock("lock:product:" + id);
        try {
            boolean isLocked = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("Could not acquire distributed lock for product: " + id);
            }
            
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
            
            if (product.getStockQuantity() < quantity) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm đã hết hàng");
            }
            
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock acquisition interrupted", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        Product product = Product.builder()
                .name(requestDTO.getName())
                .price(requestDTO.getPrice())
                .stockQuantity(requestDTO.getStockQuantity())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponseDTO(savedProduct);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return mapToResponseDTO(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        product.setName(requestDTO.getName());
        product.setPrice(requestDTO.getPrice());
        product.setStockQuantity(requestDTO.getStockQuantity());
        Product updatedProduct = productRepository.save(product);
        return mapToResponseDTO(updatedProduct);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
