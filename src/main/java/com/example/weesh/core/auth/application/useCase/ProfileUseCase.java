package com.example.weesh.core.auth.application.useCase;

import com.example.weesh.web.auth.dto.ProfileResponseDto;

public interface ProfileUseCase {
    ProfileResponseDto getProfileWithPortfolios(String username);
}
