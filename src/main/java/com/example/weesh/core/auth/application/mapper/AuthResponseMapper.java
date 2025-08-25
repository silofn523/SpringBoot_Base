package com.example.weesh.core.auth.application.mapper;

import com.example.weesh.web.auth.dto.LogoutResponseDto;
import com.example.weesh.web.auth.dto.ProfileResponseDto;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthResponseMapper {
    public ProfileResponseDto toProfileResponseDto(Map<String, Object> userData) {
        return new ProfileResponseDto(userData);
    }

    public void toLogoutResponseDto(String message) {
        new LogoutResponseDto(message);
    }
}
