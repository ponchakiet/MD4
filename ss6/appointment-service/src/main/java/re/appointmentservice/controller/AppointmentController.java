package re.appointmentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import re.appointmentservice.dto.ApiResponseError;
import re.appointmentservice.dto.AppointmentRequestDTO;
import re.appointmentservice.entity.Appointment;
import re.appointmentservice.service.AppointmentService;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> addAppointment(@RequestBody AppointmentRequestDTO dto) {
        try {
            Appointment saved = appointmentService.createAppointment(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (RuntimeException e) {

            // Trường hợp 1: Lỗi do Service liên quan bị sập (lỗi 503)
            if ("SERVICE_UNAVAILABLE".equals(e.getMessage())) {
                ApiResponseError apiError = ApiResponseError.builder()
                        .timestamp(System.currentTimeMillis())
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                        .message("Hệ thống không thể kết nối tới dịch vụ bác sĩ. Vui lòng thử lại sau.")
                        .build();
                return new ResponseEntity<>(apiError, HttpStatus.SERVICE_UNAVAILABLE);
            }

            // Trường hợp 2: Các lỗi logic nghiệp vụ khác (lỗi 400)
            ApiResponseError apiError = ApiResponseError.builder()
                    .timestamp(System.currentTimeMillis())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(e.getMessage()) // Ví dụ: "Bệnh nhân không tồn tại"
                    .build();
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
    }
}