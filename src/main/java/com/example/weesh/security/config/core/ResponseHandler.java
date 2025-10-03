package com.example.weesh.security.config.core;

import com.example.weesh.core.shared.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class ResponseHandler {
    private final ObjectMapper objectMapper;

    public void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.error(message, 401); // status를 401로 수정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 상태코드 401 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}