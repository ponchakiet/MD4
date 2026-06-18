package re.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import re.orderservice.dto.response.ProductResponse;
import re.orderservice.exception.ProductServiceUnavailableException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    public ProductResponse getProductFromProductService(String productId) {
        List<ServiceInstance> instances = discoveryClient.getInstances("PRODUCT-SERVICE");

        if (instances == null || instances.isEmpty()) {
            throw new ProductServiceUnavailableException("Hệ thống đang quá tải, yêu cầu của bạn đã được ghi nhận nhưng chưa thể hoàn tất kiểm tra kho. Vui lòng quay lại sau 1 phút.");
        }

        ServiceInstance productInstance = instances.get(0);
        String targetUrl = productInstance.getUri().toString() + "/api/v1/products/" + productId;

        try {
            return restClient.get()
                    .uri(targetUrl)
                    .retrieve()
                    .body(ProductResponse.class);
        } catch (Exception e) {
            // Bắt mọi lỗi kết nối, timeout hoặc phản hồi lỗi từ Product-Service
            throw new ProductServiceUnavailableException("Hệ thống đang quá tải, yêu cầu của bạn đã được ghi nhận nhưng chưa thể hoàn tất kiểm tra kho. Vui lòng quay lại sau 1 phút.");
        }
    }

    public ProductResponse getProductFromProductService2(String productId) {
        String targetUrl = "http://PRODUCT-SERVICE/api/v1/products/" + productId;

        try {
            return restClient.get()
                    .uri(targetUrl)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new ProductServiceUnavailableException("Hệ thống đang quá tải...");
                    })
                    .body(ProductResponse.class);
        } catch (Exception e) {
            throw new ProductServiceUnavailableException(
                    "Hệ thống đang quá tải, yêu cầu của bạn đã được ghi nhận nhưng chưa thể hoàn tất kiểm tra kho. Vui lòng quay lại sau 1 phút."
            );
        }
    }
}
