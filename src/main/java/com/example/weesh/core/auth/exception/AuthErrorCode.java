package com.example.weesh.core.auth.exception;

public final class AuthErrorCode {
    public static final int INVALID_TOKEN = 401;
    public static final int EXPIRED_TOKEN = 401;
    public static final int MISSING_TOKEN = 401;
    public static final int INVALID_CREDENTIALS = 401;
    public static final int USER_NOT_FOUND = 404;
    public static final int REFRESH_TOKEN_NOT_ALLOWED = 403;
}
