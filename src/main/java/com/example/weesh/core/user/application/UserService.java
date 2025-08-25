package com.example.weesh.core.user.application;

import com.example.weesh.core.user.application.factory.UserFactory;
import com.example.weesh.core.user.application.mapper.UserResponseMapper;
import com.example.weesh.core.user.application.useCase.RegisterUserUseCase;
import com.example.weesh.core.user.domain.User;
import com.example.weesh.core.user.exception.DuplicateUserException;
import com.example.weesh.web.user.dto.UserRequestDto;
import com.example.weesh.web.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase {
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final UserResponseMapper responseMapper;
    // 추상체 분리 할 예정
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserResponseDto register(UserRequestDto dto) {
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        User user = userFactory.createUserFromDto(dto, encryptedPassword);
        validateUser(user);
        return responseMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponseDto registerAdmin(UserRequestDto dto) {
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        User user = userFactory.createAdminFromDto(dto, encryptedPassword);
        validateUser(user);
        return responseMapper.toResponseDto(userRepository.save(user));
    }

    private void validateUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUserException("username", user.getUsername());
        }
        if (userRepository.existsByStudentNumber(user.getStudentNumber())) {
            throw new DuplicateUserException("studentNumber", String.valueOf(user.getStudentNumber()));
        }
    }
}
