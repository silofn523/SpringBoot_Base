package com.example.weesh.core.user.application.mapper;

import com.example.weesh.core.user.domain.User;
import com.example.weesh.web.user.dto.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapper {
    public UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(user);
    }
}
