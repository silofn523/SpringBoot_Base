// User 도메인 모델, 최소한의 JPA 사용
package com.example.weesh.core.user.domain;

import com.example.weesh.core.foundation.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
public class User {
    private final Long id;
    private final String username;
    private final String password;
    private final String fullName;
    private final int studentNumber;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;
    private final Set<UserRole> roles;

    @Builder
    public User(Long id, String username, String password, String fullName, int studentNumber,
                LocalDateTime createdDate, LocalDateTime lastModifiedDate, Set<UserRole> roles) {
        // 기본 검증
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
        Objects.requireNonNull(fullName, "Full name cannot be null");

        this.id = id;
        this.username = username.trim();
        this.password = password;
        this.fullName = fullName.trim();
        this.studentNumber = studentNumber;

        LocalDateTime now = LocalDateTime.now();
        this.createdDate = createdDate != null ? createdDate : now;
        this.lastModifiedDate = lastModifiedDate != null ? lastModifiedDate : now;

        // 불변 컬렉션으로 보호
        this.roles = roles != null ?
                Collections.unmodifiableSet(new HashSet<>(roles)) :
                Collections.emptySet();
    }

    // 도메인 로직
    public boolean hasRole(UserRole role) {
        return roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }

    // 업데이트를 위한 빌더 생성
    public User withUpdatedInfo(String fullName, String password) {
        return User.builder()
                .id(this.id)
                .username(this.username)
                .password(password != null ? password : this.password)
                .fullName(fullName != null ? fullName : this.fullName)
                .studentNumber(this.studentNumber)
                .createdDate(this.createdDate)
                .lastModifiedDate(LocalDateTime.now())
                .roles(new HashSet<>(this.roles))
                .build();
    }
}