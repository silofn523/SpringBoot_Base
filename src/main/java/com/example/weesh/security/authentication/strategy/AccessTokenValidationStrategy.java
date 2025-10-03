package com.example.weesh.security.authentication.strategy;

import com.example.weesh.core.auth.application.token.TokenResolver;
import com.example.weesh.core.auth.application.token.TokenStorage;
import com.example.weesh.core.auth.application.token.TokenValidator;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.security.authentication.context.AuthenticationContextManager;
import com.example.weesh.security.authentication.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTokenValidationStrategy implements TokenValidationStrategy {
    private final TokenValidator tokenValidator;
    private final TokenResolver tokenResolver;
    private final TokenStorage tokenStorage;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationContextManager authContextManager;

    @Override
    public void validate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = tokenResolver.resolveToken(request);
        if (token == null) {
            return; // 토큰이 없으면 다음 단계로 진행
        }

        // 블랙리스트 확인
        if (tokenStorage.isTokenBlacklisted(token)) {
            LoggingUtil.warn("Blacklisted token detected: {}", token.substring(0, Math.min(10, token.length())));
            throw new IllegalStateException("블랙리스트에 등록된 토큰입니다.");
        }

        // 토큰 유효성 검증
        tokenValidator.validateToken(token);

        // 인증 컨텍스트 설정
        String username = tokenValidator.getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails != null) {
            authContextManager.setAuthentication(userDetails, request);
        }
    }
}
