package xyz.catuns.spring.jwt.core.exception;

public class JwtException extends RuntimeException {

    // todo: create exception code for controller advice

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtException(String message) {
        super(message);
    }

    public JwtException() {
        super();
    }
}
