package re.productservice.service;


import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import re.productservice.entity.Product;
import re.productservice.repository.ProductRepository;

import java.util.concurrent.TimeUnit;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    private RedissonClient redissonClient;

    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        System.out.println("==> [DATABASE] Đang truy vấn SQL để lấy sản phẩm ID: " + id);
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
    }

    @CacheEvict(value = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        System.out.println("==> [DATABASE] Đang cập nhật thông tin sản phẩm ID: " + product.getId() + " vào DB");

        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại để cập nhật!"));

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setPrice(product.getPrice());

        return productRepository.save(existingProduct);
    }

    public void purchaseProduct(Long productId, int quantityToBuy) {
        String lockKey = "lock:product:" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLockAcquired = false;
        try {

            isLockAcquired = lock.tryLock(3, 5, TimeUnit.SECONDS);

            if (isLockAcquired) {
                System.out.println("==> [" + Thread.currentThread().getName() + "] Đã chiếm LOCK thành công. Bắt đầu kiểm kho...");
                Thread.sleep(1500);

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

                if (product.getStock() < quantityToBuy) {
                    throw new RuntimeException("Sản phẩm " + product.getName() + " đã hết hàng hoặc không đủ số lượng!");
                }

                product.setStock(product.getStock() - quantityToBuy);
                productRepository.save(product);

                System.out.println("==> [" + Thread.currentThread().getName() + "] ĐẶT HÀNG THÀNH CÔNG! Số lượng kho còn lại: " + product.getStock());

            } else {
                System.out.println("❌ [" + Thread.currentThread().getName() + "] Thất bại: Hệ thống đang quá tải do lượt truy cập lớn!");
                throw new RuntimeException("Hệ thống đang bận xử lý lượt mua trước, vui lòng thử lại sau giây lát!");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Tiến trình mua hàng bị ngắt quãng ngoài ý muốn.");
        } finally {
            if (isLockAcquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println("==> [" + Thread.currentThread().getName() + "] Đã GIẢI PHÓNG LOCK an toàn.");
            }
        }
    }

    // Hàm giải phóng Cache dựa trên ID nhận được từ Pub/Sub
    public void clearProductCache(Long productId) {
        if (cacheManager.getCache("products") != null) {
            cacheManager.getCache("products").evict(productId);
            System.out.println("==> XÓA CACHE THÀNH CÔNG cho sản phẩm ID: " + productId);
        }
    }
}
