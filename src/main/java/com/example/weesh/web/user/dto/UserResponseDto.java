package com.example.weesh.web.user.dto;

import com.example.weesh.core.user.domain.User;
import com.example.weesh.core.foundation.enums.UserRole;
import lombok.Getter;

import java.util.Set;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String username;
    private final String fullName;
    private final int studentNumber;
    private final Set<UserRole> roles;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername(); // 임시, 이후 비즈니스 메서드로 대체
        this.fullName = user.getFullName();
        this.studentNumber = user.getStudentNumber();
        this.roles = user.getRoles();
    }
}