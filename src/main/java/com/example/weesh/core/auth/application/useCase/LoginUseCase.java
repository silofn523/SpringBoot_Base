package com.example.weesh.core.auth.application.useCase;

import com.example.weesh.data.jwt.JwtTokenResponse;
import com.example.weesh.web.auth.dto.AuthRequestDto;

public interface LoginUseCase {
    JwtTokenResponse login(AuthRequestDto dto);
}
