package com.example.weesh.security.authentication.strategy;

import com.example.weesh.core.auth.application.token.TokenResolver;
import com.example.weesh.core.auth.application.token.TokenValidator;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.security.authorization.validator.PathValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenValidationStrategy implements TokenValidationStrategy {
    private final TokenResolver tokenResolver;
    private final PathValidator pathValidator;
    private final TokenValidator tokenValidator;

    @Override
    public void validate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refreshToken = tokenResolver.resolveRefreshToken(request);
        if (refreshToken == null) {
            return; // 리프레시 토큰이 없으면 다음 단계로 진행
        }

        String requestURI = request.getRequestURI();
        if (!pathValidator.isRefreshTokenAllowed(requestURI)) {
            LoggingUtil.warn("Refresh token misuse detected for URI: {}", requestURI);
            throw new IllegalStateException("리프레시 토큰은 이 엔드포인트에 사용할 수 없습니다: " + requestURI);
        }
        tokenValidator.validateToken(refreshToken);

    }
}
