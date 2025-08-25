package com.example.weesh.core.auth.application;

import com.example.weesh.core.user.domain.User;

import java.util.Optional;

public interface AuthRepository {
    Optional<User> findByUsername(String username);
}
