package com.example.weesh.security.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Getter
@Component
public class PathValidator {
    private final Set<String> publicPaths;
    private final Set<String> refreshTokenAllowedPaths;

    public PathValidator(@Value("${security.public.paths:/v3/api-docs/**,/swagger-ui/**,/users/register/**,/auth/login,/error}") String[] publicPaths,
                         @Value("${security.refresh.token.allowed.paths:/auth/reissue}") String[] refreshTokenAllowedPaths) {
        this.publicPaths = new HashSet<>(Set.of(publicPaths));
        this.refreshTokenAllowedPaths = new HashSet<>(Set.of(refreshTokenAllowedPaths));
    }

    public boolean isPublicPath(String requestURI) {
        return publicPaths.stream().anyMatch(requestURI::startsWith);
    }

    public boolean isRefreshTokenAllowed(String requestURI) {
        return refreshTokenAllowedPaths.contains(requestURI);
    }
}