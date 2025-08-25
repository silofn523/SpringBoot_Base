package com.example.weesh.core.shared;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
//
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    private final LocalDateTime timestamp;

    private final Integer status;
    private final String path;
    private final String error;

    // 기본 성공 응답 (데이터만)
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다")
                .data(data)
//                .timestamp(LocalDateTime.now())
                .status(200)
                .build();
    }

    // 성공 응답 (메시지 + 데이터)
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
//                .timestamp(LocalDateTime.now())
                .status(200)
                .build();
    }

    // 성공 응답 (상태코드 포함)
    public static <T> ApiResponse<T> success(String message, T data, int status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
//                .timestamp(LocalDateTime.now())
                .status(status)
                .build();
    }

    // 성공 응답 (경로 포함)
    public static <T> ApiResponse<T> success(String message, T data, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
//                .timestamp(LocalDateTime.now())
                .status(200)
                .path(path)
                .build();
    }

    // 오류 응답 (상태코드 포함)
    public static <T> ApiResponse<T> error(String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
//                .timestamp(LocalDateTime.now())
                .status(status)
                .error(getErrorName(status))
                .build();
    }

    // 오류 응답 (상태코드 + 경로 포함)
    public static <T> ApiResponse<T> error(String message, int status, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
//                .timestamp(LocalDateTime.now())
                .status(status)
                .error(getErrorName(status))
                .path(path)
                .build();
    }

    // 완전한 오류 응답 (모든 정보 포함)
    public static <T> ApiResponse<T> error(String message, int status, String error, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
//                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .path(path)
                .build();
    }

    // 상태코드에 따른 에러명 반환
    private static String getErrorName(int status) {
        return switch (status) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 409 -> "Conflict";
            case 422 -> "Unprocessable Entity";
            case 429 -> "Too Many Requests";
            case 500 -> "Internal Server Error";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            default -> "Error";
        };
    }
}