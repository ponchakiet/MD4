package re.productservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.productservice.entity.Product;
import re.productservice.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping
    public Product updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<String> purchase(@PathVariable Long id, @RequestParam int quantity) {
        try {
            productService.purchaseProduct(id, quantity);
            return ResponseEntity.ok("Giao dịch mua hàng thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Giao dịch thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/promotions/change")
    public String changePromotion(@RequestParam Long productId) {
        messagePublisher.publishPromotionUpdate(productId);
        return "Đã đổi chương trình khuyến mãi và phát tín hiệu đồng bộ!";
    }
}