package xyz.catuns.spring.jwt.security.exception;

public class JwtSecurityException extends RuntimeException {
    public JwtSecurityException(Exception exception) {
        super(exception);
    }
}
