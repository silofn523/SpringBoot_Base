package com.example.weesh.core.auth.application.jwt;

import com.example.weesh.data.jwt.JwtTokenResponse;

public interface TokenGenerator {
    JwtTokenResponse generateToken(String username, Long userId);
}
