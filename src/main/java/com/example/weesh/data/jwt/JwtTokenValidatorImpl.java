package com.example.weesh.data.jwt;

import com.example.weesh.core.auth.application.jwt.TokenValidator;
import com.example.weesh.core.auth.exception.AuthErrorCode;
import com.example.weesh.core.auth.exception.AuthException;
import com.example.weesh.core.foundation.log.LoggingUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenValidatorImpl implements TokenValidator {
    private final SecretKey key;

    public JwtTokenValidatorImpl(@Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalStateException("JWT secret key is not configured properly");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "토큰이 비어있거나 누락되었습니다.");
        }

        try {
            Claims claims = parseToken(token);
            validateExpiration(claims);
        } catch (SecurityException | SignatureException e) {
            LoggingUtil.warn("Invalid JWT signature: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "잘못된 토큰 서명입니다.");
        } catch (ExpiredJwtException e) {
            LoggingUtil.warn("Expired JWT token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN, "토큰이 만료되었습니다.");
        } catch (MalformedJwtException e) {
            LoggingUtil.warn("Malformed JWT token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "잘못된 형식의 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            LoggingUtil.warn("Unsupported JWT token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "지원하지 않는 토큰 형식입니다.");
        } catch (IllegalArgumentException e) {
            LoggingUtil.warn("Invalid JWT token argument: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다.");
        } catch (JwtException e) {
            LoggingUtil.warn("JWT processing error: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "토큰 처리 중 오류가 발생했습니다.");
        } catch (Exception e) {
            LoggingUtil.error("Unexpected error during token validation: {}", e.getMessage(), String.valueOf(e));
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "토큰 검증 중 예상치 못한 오류가 발생했습니다.");
        }
    }

    @Override
    public String getUsername(String token) {
        try {
            return parseToken(token).getSubject();
        } catch (Exception e) {
            LoggingUtil.error("Failed to extract username from token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "토큰에서 사용자 정보를 추출할 수 없습니다.");
        }
    }

    @Override
    public String getTokenType(String token) {
        validateToken(token);
        try {
            return parseToken(token).get("type", String.class);
        } catch (Exception e) {
            LoggingUtil.error("Failed to extract token type: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "토큰 타입을 확인할 수 없습니다.");
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void validateExpiration(Claims claims) {
        Date now = new Date();
        if (claims.getExpiration().before(now)) {
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN, "토큰이 만료되었습니다.");
        }
    }
}
