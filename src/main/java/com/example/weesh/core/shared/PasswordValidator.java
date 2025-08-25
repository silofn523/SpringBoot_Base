package com.example.weesh.core.shared;

public interface PasswordValidator {
    void validate(String rawPassword, String encodedPassword);
}
