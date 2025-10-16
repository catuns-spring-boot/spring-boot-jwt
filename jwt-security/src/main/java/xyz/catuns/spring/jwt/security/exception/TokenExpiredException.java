package xyz.catuns.spring.jwt.security.exception;

import io.jsonwebtoken.ExpiredJwtException;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(ExpiredJwtException exception) {
        super(exception.getMessage(), exception);
    }
}
