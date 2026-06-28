package re.pharmacyservice.service;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InvoiceService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Kết hợp 2 lớp bảo vệ
    @Retry(name = "invoiceRetry", fallbackMethod = "invoiceFallback")
    @RateLimiter(name = "invoiceLimit")
    public String createInvoice(Object invoiceData) {
        System.out.println("Đang thực hiện xuất hóa đơn...");
        String url = "http://ELECTRONIC-INVOICE-SERVICE/api/v1/invoices";
        return restTemplate.postForObject(url, invoiceData, String.class);
    }

    public String invoiceFallback(Object invoiceData, Exception e) {
        if (e instanceof io.github.resilience4j.ratelimiter.RequestNotPermitted) {
            return "Thao tác quá nhanh! Bạn chỉ được xuất 5 hóa đơn mỗi 10 giây.";
        }
        return "Hệ thống hóa đơn điện tử đang trục trặc. Đã thử lại 3 lần nhưng không thành công. Vui lòng lưu nháp và gửi lại sau!";
    }
}