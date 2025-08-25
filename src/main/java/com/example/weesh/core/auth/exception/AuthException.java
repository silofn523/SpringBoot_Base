package com.example.weesh.core.auth.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final int statusCode;
    private final String message;

    public AuthException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }
}
