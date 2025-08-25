package com.example.weesh.core.user.application.factory;

import com.example.weesh.core.user.domain.User;
import com.example.weesh.web.user.dto.UserRequestDto;

public interface UserFactory {
    User createUserFromDto(UserRequestDto requestDto, String encryptedPassword);
    User createAdminFromDto(UserRequestDto requestDto, String encryptedPassword);
}
