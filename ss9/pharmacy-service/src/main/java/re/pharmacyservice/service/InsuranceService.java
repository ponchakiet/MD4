package re.pharmacyservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@RefreshScope
public class InsuranceService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    @CircuitBreaker(name = "insuranceCB")
    @TimeLimiter(name = "insuranceTL")
    @Retry(name = "insuranceRetry", fallbackMethod = "insuranceFallback")
    public CompletableFuture<String> verifyInsurance(String cardId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = "http://INSURANCE-SERVER/verify/" + cardId;
            return restTemplate.getForObject(url, String.class);
        });
    }

    // Hàm Fallback cuối cùng
    public CompletableFuture<String> insuranceFallback(String cardId, Exception e) {
        return CompletableFuture.completedFuture(
                "Giá thuốc chưa chiết khấu (Xác thực bảo hiểm sau do hệ thống chậm/lỗi)."
        );
    }
}