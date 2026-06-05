package xyz.catuns.spring.jwt.security.exception;

import io.jsonwebtoken.ExpiredJwtException;
import xyz.catuns.spring.jwt.core.exception.JwtSecurityException;

public class TokenExpiredException extends JwtSecurityException {
    public TokenExpiredException(ExpiredJwtException exception) {
        super(exception.getMessage(), exception);
    }
}
