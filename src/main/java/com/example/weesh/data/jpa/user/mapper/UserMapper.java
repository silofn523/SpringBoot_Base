package com.example.weesh.data.jpa.user.mapper;

import com.example.weesh.core.user.domain.User;
import com.example.weesh.data.jpa.user.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .fullName(entity.getFullName())
                .studentNumber(entity.getStudentNumber())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .roles(entity.getRoles() != null ? new HashSet<>(entity.getRoles()) : new HashSet<>())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setFullName(user.getFullName());
        entity.setStudentNumber(user.getStudentNumber());

        // 생성/수정 시간 처리
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDate(user.getCreatedDate() != null ? user.getCreatedDate() : now);
        entity.setLastModifiedDate(user.getLastModifiedDate() != null ? user.getLastModifiedDate() : now);

        entity.setRoles(user.getRoles() != null ? new HashSet<>(user.getRoles()) : new HashSet<>());
        return entity;
    }
}
