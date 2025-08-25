package com.example.weesh.core.user.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String field, String value) {
        super(String.format("사용자 정보가 중복되었습니다. %s: %s", field, value));
    }
}