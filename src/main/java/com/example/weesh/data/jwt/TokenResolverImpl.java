package com.example.weesh.data.jwt;

import com.example.weesh.core.auth.application.jwt.TokenResolver;
import com.example.weesh.core.auth.application.jwt.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenResolverImpl implements TokenResolver {
    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenValidator tokenValidator;

    @Override
    public String resolveToken(HttpServletRequest request) {
        return extractTokenFromHeader(request.getHeader("Authorization"), "access");
    }

    @Override
    public String resolveRefreshToken(HttpServletRequest request) {
        String token = extractTokenFromHeader(request.getHeader("Authorization"), null);
        if (token != null && "refresh".equals(getTokenType(token))) {
            return token;
        }
        return null;
    }

    private String extractTokenFromHeader(String bearerToken, String expectedType) {
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(BEARER_PREFIX)) {
            return null;
        }
        String token = bearerToken.substring(BEARER_PREFIX.length());
        if (expectedType != null && !expectedType.equals(getTokenType(token))) {
            return null;
        }
        return token;
    }

    private String getTokenType(String token) {
        return tokenValidator.getTokenType(token);
    }
}
