package re.pharmacyservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PharmacyService {

    private final RestTemplate restTemplate = new RestTemplate();

    @CircuitBreaker(name = "warehouseCB", fallbackMethod = "checkWarehouseFallback")
    public String checkStockFromWarehouse(Long medicineId) {
        String url = "http://WAREHOUSE-SERVICE/api/v1/warehouse/check/" + medicineId;
        return restTemplate.getForObject(url, String.class);
    }

    public String checkWarehouseFallback(Long medicineId, Exception e) {
        System.err.println("Lỗi kết nối kho tổng: " + e.getMessage());
        return "Không thể kết nối kho tổng. Hệ thống sẽ sử dụng dữ liệu tồn kho cục bộ để tiếp tục giao dịch.";
    }
}