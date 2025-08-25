package com.example.weesh.data.jpa.auth;

import com.example.weesh.core.auth.application.AuthRepository;
import com.example.weesh.core.user.domain.User;
import com.example.weesh.data.jpa.user.JpaUserRepositoryImpl;
import com.example.weesh.data.jpa.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

    private final JpaUserRepositoryImpl jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }
}