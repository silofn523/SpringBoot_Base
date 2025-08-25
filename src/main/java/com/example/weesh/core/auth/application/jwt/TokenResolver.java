package com.example.weesh.core.auth.application.jwt;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenResolver {
    String resolveRefreshToken(HttpServletRequest request);
    String resolveToken(HttpServletRequest request);
}
