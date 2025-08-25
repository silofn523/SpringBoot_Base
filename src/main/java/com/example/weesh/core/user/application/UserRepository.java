package com.example.weesh.core.user.application;

import com.example.weesh.core.user.domain.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByStudentNumber(int studentNumber);
}