package com.example.weesh.data.jwt;

import com.example.weesh.core.auth.application.jwt.TokenGenerator;
import com.example.weesh.core.auth.application.jwt.TokenStorage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenGeneratorImpl implements TokenGenerator {
    private final SecretKey key;
    private static final long ACCESS_TOKEN_VALID_TIME = 30 * 60 * 1000L; // 30분
    private static final long REFRESH_TOKEN_VALID_TIME = 14 * 24 * 60 * 60 * 1000L; // 14일
    private final TokenStorage tokenStorage;

    public JwtTokenGeneratorImpl(@Value("${jwt.secret}") String secretKey, TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalStateException("JWT secret key is not configured properly");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public JwtTokenResponse generateToken(String username, Long userId) {
        Date now = new Date();

        String accessToken = createAccessToken(username, userId, now);
        String refreshToken = createRefreshToken(username, userId, now);

        setRefreshToken(username, refreshToken);
        return new JwtTokenResponse(accessToken, refreshToken, "Bearer", ACCESS_TOKEN_VALID_TIME);
    }

    private String createAccessToken(String username, Long userId, Date issuedAt) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add("userId", userId)
                .add("type", "access")
                .build();

        Date expiration = new Date(issuedAt.getTime() + ACCESS_TOKEN_VALID_TIME);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private String createRefreshToken(String username, Long userId, Date issuedAt) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add("userId", userId)
                .add("type", "refresh")
                .build();

        Date expiration = new Date(issuedAt.getTime() + REFRESH_TOKEN_VALID_TIME);

        return Jwts.builder()
                .claims(claims)
                .expiration(expiration)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private void setRefreshToken(String username, String refreshToken) {
        tokenStorage.setRefreshToken(username, refreshToken, REFRESH_TOKEN_VALID_TIME);
    }
}