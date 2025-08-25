package com.example.weesh.core.auth.application.jwt;

public interface TokenValidator {
    void validateToken(String token);
    String getUsername(String token);
    String getTokenType(String token);
}
