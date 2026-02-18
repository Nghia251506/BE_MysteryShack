package org.example.be_eproject_sem4.Exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt lỗi Validation (nếu ông có dùng @Valid ở Controller)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    // 2. Bắt lỗi Duplicate Entry (Email/Username đã tồn tại) - CÁI ÔNG ĐANG CẦN
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        String message = ex.getMostSpecificCause().getMessage();

        // Check xem lỗi trùng lặp ở field nào (tên constraint trong DB)
        if (message.contains("email")) {
            errors.put("email", "Email này đã được sử dụng rồi!");
        } else if (message.contains("username")) {
            errors.put("username", "Tên đăng nhập này đã có người chiếm đóng!");
        } else {
            errors.put("error", "Dữ liệu bị trùng lặp hoặc không hợp lệ!");
        }

        return ResponseEntity.badRequest().body(errors);
    }

    // 3. Bắt các lỗi Runtime chung chung khác
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }
}