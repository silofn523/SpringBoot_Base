package com.example.weesh.data.jwt;

import com.example.weesh.core.auth.application.jwt.TokenStorage;
import com.example.weesh.core.auth.exception.AuthErrorCode;
import com.example.weesh.core.auth.exception.AuthException;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.data.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisTokenStorageImpl implements TokenStorage {
    private final RedisService redisService;

    @Override
    public String getStoredRefreshToken(String username) {
        try {
            return redisService.getValues(refreshTokenKey(username));
        } catch (Exception e) {
            LoggingUtil.error("Failed to get refresh token for username: {}", username, String.valueOf(e));
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "리프레시 토큰 조회 실패");
        }
    }

    @Override
    public void invalidateRefreshToken(String username) {
        try {
            redisService.deleteValues(refreshTokenKey(username));
            LoggingUtil.info("Refresh token invalidated for username: {}", username);
        } catch (Exception e) {
            LoggingUtil.error("Failed to invalidate refresh token for username: {}", username, String.valueOf(e));
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "리프레시 토큰 무효화 실패");
        }
    }

    @Override
    public void blacklistAccessToken(String accessToken) {
        try {
            // 토큰의 만료 시간 계산 후 TTL 설정
            long ttl = calculateTokenTTL(accessToken);
            if (ttl > 0) {
                redisService.setValues(blacklistKey(accessToken), "logout", Duration.ofMillis(ttl));
                LoggingUtil.info("Access token blacklisted");
            }
        } catch (Exception e) {
            LoggingUtil.error("Failed to blacklist access token", String.valueOf(e));
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "토큰 블랙리스트 등록 실패");
        }
    }

    @Override
    public void setRefreshToken(String username, String refreshToken, long validityMillis) {
        try {
            redisService.setValues(refreshTokenKey(username), refreshToken, Duration.ofMillis(validityMillis));
            LoggingUtil.info("Stored refresh token for username: {}", username);
        } catch (Exception e) {
            LoggingUtil.error("Failed to store refresh token for username: {}", username, String.valueOf(e));
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "리프레시 토큰 저장 실패");
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            String blacklisted = redisService.getValues(blacklistKey(token));
            return blacklisted != null;
        } catch (Exception e) {
            LoggingUtil.error("Failed to check token blacklist status", String.valueOf(e));
            return false; // 에러 시 false 반환하여 인증 플로우 계속 진행
        }
    }

    private String refreshTokenKey(String username) {
        return "refresh_token:" + username;
    }

    private String blacklistKey(String token) {
        return "blacklist:" + token;
    }

    private long calculateTokenTTL(String token) {
        // 실제 구현에서는 JWT 파서를 사용하여 만료 시간 계산
        // 여기서는 간단히 30분으로 설정
        return 30 * 60 * 1000L;
    }
}