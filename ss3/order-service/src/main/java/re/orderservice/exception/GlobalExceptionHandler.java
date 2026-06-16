package re.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import re.orderservice.dto.response.ApiResponseError;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Bắt lỗi Validation (Quantity <= 0) -> Trả về 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseError> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldError().getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, msg);
    }

    // Bắt các lỗi runtime hoặc lỗi Logic khác -> Trả về 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseError> handleBadRequest(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Bắt tất cả các lỗi không mong đợi -> Trả về 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseError> handleGlobalError(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred");
    }

    private ResponseEntity<ApiResponseError> buildResponse(HttpStatus status, String message) {
        ApiResponseError error = new ApiResponseError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
        return new ResponseEntity<>(error, status);
    }
}
