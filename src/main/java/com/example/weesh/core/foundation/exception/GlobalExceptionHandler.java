package com.example.weesh.core.foundation.exception;

import com.example.weesh.core.auth.exception.AuthException;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.core.user.exception.DuplicateUserException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.security.SignatureException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateUserException(
            DuplicateUserException ex, HttpServletRequest request) {
        LoggingUtil.error("Handling duplicate user exception: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        LoggingUtil.error("Validation error: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                "입력값이 올바르지 않습니다: " + ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("입력값이 올바르지 않습니다.");

        LoggingUtil.error("Validation error: " + errorMessage, String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                errorMessage,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<?>> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {
        LoggingUtil.error("Response status error: " + ex.getReason(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                ex.getReason(),
                ex.getStatusCode().value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleBlacklistException(
            IllegalStateException ex, HttpServletRequest request) {
        LoggingUtil.error("IllegalStateException error: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthException(
            AuthException ex, HttpServletRequest request) {
        LoggingUtil.error("Auth error: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                ex.getMessage(),
                ex.getStatusCode(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatusCode()));
    }

    // JWT 라이브러리 예외들 직접 처리
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse<?>> handleSignatureException(
            SignatureException ex, HttpServletRequest request) {
        LoggingUtil.error("JWT signature error: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                "잘못된 토큰입니다.",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<?>> handleExpiredJwtException(
            ExpiredJwtException ex, HttpServletRequest request) {
        LoggingUtil.error("JWT expired error: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                "토큰이 만료되었습니다. 다시 로그인해주세요.",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse<?>> handleMalformedJwtException(
            MalformedJwtException ex, HttpServletRequest request) {
        LoggingUtil.error("JWT malformed error: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                "잘못된 형식의 토큰입니다.",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiResponse<?>> handleUnsupportedJwtException(
            UnsupportedJwtException ex, HttpServletRequest request) {
        LoggingUtil.error("JWT unsupported error: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                "지원하지 않는 토큰 형식입니다.",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(
            JwtException ex, HttpServletRequest request) {
        LoggingUtil.error("JWT processing error: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                "토큰 처리 중 오류가 발생했습니다.",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        LoggingUtil.error("Internal server error: " + ex.getMessage(), String.valueOf(ex));

        ApiResponse<?> response = ApiResponse.error(
                "서버 내부 오류가 발생했습니다.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}