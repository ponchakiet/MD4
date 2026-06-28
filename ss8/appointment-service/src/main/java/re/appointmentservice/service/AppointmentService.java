package re.appointmentservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import re.appointmentservice.dto.ApiResponseError;
import re.appointmentservice.dto.AppointmentRequestDTO;
import re.appointmentservice.entity.Appointment;
import re.appointmentservice.repository.AppointmentRepository;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final RestTemplate restTemplate;

    public Appointment createAppointment(AppointmentRequestDTO dto) {
        Boolean isPatientExist = restTemplate.getForObject(
                "http://PATIENT-SERVICE/api/v1/patients/exists/" + dto.getPatientId(), Boolean.class);

        Boolean isDoctorExist = restTemplate.getForObject(
                "http://DOCTOR-SERVICE/api/v1/doctors/exists/" + dto.getDoctorId(), Boolean.class);

        if (Boolean.TRUE.equals(isPatientExist) && Boolean.TRUE.equals(isDoctorExist)) {
            Appointment appointment = Appointment.builder()
                    .patientId(dto.getPatientId())
                    .doctorId(dto.getDoctorId())
                    .appointmentDate(dto.getAppointmentDate())
                    .reason(dto.getReason())
                    .status("PENDING")
                    .build();
            return appointmentRepository.save(appointment);
        } else {
            throw new RuntimeException("Thông tin Bệnh nhân hoặc Bác sĩ không hợp lệ!");
        }
    }

    // Tên fallbackMethod phải trùng khớp với tên hàm ở dưới
    @CircuitBreaker(name = "doctorServiceCB", fallbackMethod = "getDoctorFallback")
    public Object checkDoctorSchedule(Long doctorId) {
        String url = "http://DOCTOR-SERVICE/doctors/" + doctorId + "/schedule";
        return restTemplate.getForObject(url, Object.class);
    }

    // Hàm dự phòng (Fallback)
    // Lưu ý: Signature (tham số) phải giống hàm chính, cộng thêm tham số Exception
    public Object getDoctorFallback(Long doctorId, Exception e) {
        ApiResponseError errorResponse = new ApiResponseError();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(503);
        errorResponse.setError("Service Unavailable");
        errorResponse.setMessage("Hiện tại không thể kiểm tra thông tin bác sĩ, vui lòng thử lại sau vài giây");

        return errorResponse;
    }

    // 1. Áp dụng Retry: Nếu lỗi, nó sẽ tự động chạy lại hàm này tối đa 3 lần
    // 2. Nếu sau 3 lần vẫn lỗi, nó sẽ nhảy vào hàm fallback
    @Retry(name = "patientRetry", fallbackMethod = "getPatientFallback")
    public Object getPatientInfo(Long patientId) {
        System.out.println("Đang thực hiện gọi sang PATIENT-SERVICE...");
        String url = "http://PATIENT-SERVICE/patients/" + patientId;
        return restTemplate.getForObject(url, Object.class);
    }

    // Hàm dự phòng khi Retry thất bại hoàn toàn
    public Object getPatientFallback(Long patientId, Exception e) {
        ApiResponseError errorResponse = new ApiResponseError();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(503);
        errorResponse.setError("Service Unavailable");
        errorResponse.setMessage("Hệ thống bệnh nhân đang bận, đã thử lại 3 lần nhưng không thành công.");

        return errorResponse;
    }

    @Async
    @TimeLimiter(name = "insuranceTimeout", fallbackMethod = "insuranceFallback")
    public CompletableFuture<String> checkInsurance(Long patientId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = "http://INSURANCE-SERVICE/insurance/verify/" + patientId;
            // Giả lập logic gọi API có thể bị chậm
            return restTemplate.getForObject(url, String.class);
        });
    }

    // Hàm fallback xử lý khi bị Timeout
    public CompletableFuture<String> insuranceFallback(Long patientId, Exception e) {
        return CompletableFuture.completedFuture(
                "Bỏ qua kiểm tra bảo hiểm (Dịch vụ chậm). Bệnh nhân thực hiện thanh toán trực tiếp."
        );
    }
}