package com.example.weesh.data.jwt;

import lombok.Builder;

@Builder
public record JwtTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long accessTokenExpireDate
) {
    public static JwtTokenResponse of(String accessToken, String refreshToken, String tokenType, Long expireDate) {
        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .accessTokenExpireDate(expireDate)
                .build();
    }
}