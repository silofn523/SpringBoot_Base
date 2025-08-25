package com.example.weesh.core.auth.application.useCase;

public interface TokenManagementUseCase {
    String reissueAccessToken(String refreshToken);
    void logout(String accessToken);
}
