package com.godzillajim.user_management.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String token_already_expired) {
    super(token_already_expired);
    }
}
