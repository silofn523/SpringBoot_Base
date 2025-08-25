package com.example.weesh.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenValidationStrategy {
    void validate(HttpServletRequest request, HttpServletResponse response) throws Exception;
}