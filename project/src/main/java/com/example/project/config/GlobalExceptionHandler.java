package com.example.project.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)  // 모든 예외를 처리하는 핸들러
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        Map<String, Object> body = Map.of(
            "errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "errorMessage", "서버에 문제가 발생했습니다. 관리자에게 문의해주세요.",
            "requestedUrl", request.getRequestURL().toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(NullPointerException.class)   // NullPointerException을 처리하는 핸들러
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NullPointerException ex, HttpServletRequest request) {
        Map<String, Object> body = Map.of(
            "errorCode", HttpStatus.NOT_FOUND.value(),
            "errorMessage", "요청하신 페이지를 찾을 수 없습니다.",
            "requestedUrl", request.getRequestURL().toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)  // 권한이 없는 접근을 처리하는 핸들러
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        Map<String, Object> body = Map.of(
            "errorCode", HttpStatus.FORBIDDEN.value(),
            "errorMessage", "접근 권한이 없습니다.",
            "requestedUrl", request.getRequestURL().toString()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)   // 잘못된 인자를 처리하는 핸들러
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        Map<String, Object> body = Map.of(
            "errorCode", HttpStatus.BAD_REQUEST.value(),
            "errorMessage", ex.getMessage(),
            "requestedUrl", request.getRequestURL().toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)    // 유효성 검사 실패를 처리하는 핸들러
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        Map<String, Object> body = Map.of(
            "errorCode", HttpStatus.BAD_REQUEST.value(),
            "errorMessage", "존재하지 않는 카테고리입니다.",
            "requestedUrl", request.getRequestURL().toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
            ));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleUnauthorizedException(UnauthorizedException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return errorResponse;
    }
}
