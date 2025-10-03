package com.example.weesh.security.password;

import com.example.weesh.core.auth.exception.AuthException;
import com.example.weesh.core.shared.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordValidatorImpl implements PasswordValidator {
    private final PasswordEncoder passwordEncoder;

    @Override
    public void validate(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthException(400, "비밀번호가 일치하지 않습니다.");
        }
    }
}
