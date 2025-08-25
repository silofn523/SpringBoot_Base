package com.example.weesh.core.user.application.useCase;

import com.example.weesh.web.user.dto.UserRequestDto;
import com.example.weesh.web.user.dto.UserResponseDto;

// useCase 인터페이스, 사용자 등록을 위한 메서드 정의
public interface RegisterUserUseCase {
    UserResponseDto register(UserRequestDto userRequestDto);
    UserResponseDto registerAdmin(UserRequestDto userRequestDto);
}
