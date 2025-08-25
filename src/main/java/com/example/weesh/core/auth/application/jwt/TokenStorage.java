package com.example.weesh.core.auth.application.jwt;

public interface TokenStorage {
    String getStoredRefreshToken(String username);
    void invalidateRefreshToken(String username);
    void blacklistAccessToken(String accessToken);
    void setRefreshToken(String username, String refreshToken, long validityMillis);
    boolean isTokenBlacklisted(String token);
}
