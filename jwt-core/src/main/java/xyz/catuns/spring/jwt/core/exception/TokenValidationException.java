package xyz.catuns.spring.jwt.core.exception;

public class TokenValidationException extends JwtException {

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenValidationException(String message) {
        super(message);
    }
    public TokenValidationException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
