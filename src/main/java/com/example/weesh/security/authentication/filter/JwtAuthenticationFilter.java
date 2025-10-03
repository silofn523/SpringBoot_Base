package com.example.weesh.security.authentication.filter;

import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.security.authentication.strategy.AccessTokenValidationStrategy;
import com.example.weesh.security.authentication.context.AuthenticationContextManager;
import com.example.weesh.security.authentication.strategy.RefreshTokenValidationStrategy;
import com.example.weesh.security.authorization.validator.PathValidator;
import com.example.weesh.security.config.core.ResponseHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final PathValidator pathValidator;
    private final AccessTokenValidationStrategy accessTokenStrategy;
    private final RefreshTokenValidationStrategy refreshTokenStrategy;
    private final AuthenticationContextManager authContextManager;
    private final ResponseHandler responseHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        try {
            if (pathValidator.isPublicPath(requestURI)) {
                if (requestURI.startsWith("/advice")) {
                    accessTokenStrategy.validate(request, response);
                    refreshTokenStrategy.validate(request, response);
                }
                chain.doFilter(request, response);
                return;
            }

            List.of(refreshTokenStrategy, accessTokenStrategy)
                    .forEach(strategy -> {
                        try {
                            strategy.validate(request, response);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    });

            chain.doFilter(request, response);
        } catch (Exception e) {
            LoggingUtil.error("Authentication error for URI: {}, message: {}", requestURI, e.getMessage());
            authContextManager.clearAuthentication();
            responseHandler.sendErrorResponse(response, e.getMessage());
            return;
        }
    }
}