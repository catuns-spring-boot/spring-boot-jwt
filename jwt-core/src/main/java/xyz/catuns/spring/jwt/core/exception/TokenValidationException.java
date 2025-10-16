package xyz.catuns.spring.jwt.core.exception;

public class TokenValidationException extends Exception {

    public TokenValidationException(Exception exception) {
        super(exception);
    }
}
