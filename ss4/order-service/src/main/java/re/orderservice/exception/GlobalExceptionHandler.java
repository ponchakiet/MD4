package re.orderservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import re.orderservice.dto.response.ApiResponseError;

import javax.management.ServiceNotFoundException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ApiResponseError> handleServiceNotFound(ServiceNotFoundException ex, HttpServletRequest request) {

        ApiResponseError errorBody = ApiResponseError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorBody, HttpStatus.SERVICE_UNAVAILABLE);
    }
    @ExceptionHandler(ProductServiceUnavailableException.class)
    public ResponseEntity<ApiResponseError> handleProductServiceError(ProductServiceUnavailableException ex, HttpServletRequest request) {

        ApiResponseError errorBody = ApiResponseError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value()) // Mã 503
                .error(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorBody);
    }

}
