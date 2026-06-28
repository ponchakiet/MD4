package re.doctorservice.controller;


import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import re.doctorservice.dto.ApiResponseError;
import re.doctorservice.dto.DoctorResponseDTO;
import re.doctorservice.service.DoctorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public List<DoctorResponseDTO> getDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/search")
    @RateLimiter(name = "searchDoctorLimit", fallbackMethod = "searchFallback")
    public ResponseEntity<String> searchDoctor(@RequestParam String name) {
        return ResponseEntity.ok("Kết quả tìm kiếm cho bác sĩ: " + name);
    }

    // Hàm fallback xử lý khi vượt quá tần suất gọi (Spam)
    public ResponseEntity<Object> searchFallback(String name, Exception e) {
        ApiResponseError errorResponse = new ApiResponseError();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(429); // Too Many Requests
        errorResponse.setError("Too Many Requests");
        errorResponse.setMessage("Bạn đã gọi quá giới hạn (5 lần/10 giây). Vui lòng đợi thêm nhé!");

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }
}
