package com.example.weesh.data.factory;

import com.example.weesh.core.foundation.exception.ValidationException;
import com.example.weesh.core.user.application.factory.UserFactory;
import com.example.weesh.core.user.domain.User;
import com.example.weesh.core.foundation.enums.UserRole;
import com.example.weesh.web.user.dto.UserRequestDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

@Component
public class UserFactoryImpl implements UserFactory {
    @Override
    public User createUserFromDto(UserRequestDto requestDto, String encryptedPassword) {
        Objects.requireNonNull(requestDto, "Request DTO cannot be null");
        validateRequestDto(requestDto);
        int studentNumber = parseStudentNumber(requestDto.getStudentNumber());
        return User.builder()
                .username(requestDto.getUsername())
                .password(encryptedPassword)
                .fullName(requestDto.getFullName())
                .studentNumber(studentNumber)
                .roles(Collections.singleton(UserRole.USER))
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    @Override
    public User createAdminFromDto(UserRequestDto requestDto, String encryptedPassword) {
        Objects.requireNonNull(requestDto, "Request DTO cannot be null");
        validateRequestDto(requestDto);
        int studentNumber = parseStudentNumber(requestDto.getStudentNumber());
        return User.builder()
                .username(requestDto.getUsername())
                .password(encryptedPassword)
                .fullName(requestDto.getFullName())
                .studentNumber(studentNumber)
                .roles(new HashSet<>(Collections.singletonList(UserRole.ADMIN))) // admin 역할 설정
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    private void validateRequestDto(UserRequestDto dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("아이디는 null 또는 빈 값일 수 없습니다.");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 null 또는 빈 값일 수 없습니다.");
        }
    }

    private int parseStudentNumber(String studentNumber) {
        if (studentNumber == null || studentNumber.trim().isEmpty()) {
            throw new ValidationException("학번은 null 또는 빈 값일 수 없습니다.");
        }
        try {
            int num = Integer.parseInt(studentNumber);
            String strNum = String.valueOf(num); // 선행 0 제거 후 확인
            if (strNum.length() != 4) {
                throw new ValidationException("학번은 4자리 숫자여야 합니다.");
            }
            char firstDigit = strNum.charAt(0);
            if (firstDigit != '1' && firstDigit != '2' && firstDigit != '3') {
                throw new ValidationException("올바른 학번이 아닙니다. 첫 자리는 1, 2, 3 중 하나여야 합니다.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new ValidationException("학번은 숫자 형식이어야 합니다.");
        }
    }
}
