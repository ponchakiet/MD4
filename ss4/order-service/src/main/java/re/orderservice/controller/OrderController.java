package re.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import re.orderservice.dto.response.ProductResponse;
import re.orderservice.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/product-info/{productId}")
    public ResponseEntity<ProductResponse> getProductInfo(@PathVariable String productId) {
        ProductResponse product = orderService.getProductFromProductService2(productId);

        return ResponseEntity.ok(product);
    }

}
