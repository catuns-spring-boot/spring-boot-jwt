package xyz.catuns.spring.jwt.core.exception;

public class JwtSecurityException extends RuntimeException {

    // todo: create exception code for controller advice

    public JwtSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtSecurityException(String message) {
        super(message);
    }

    public JwtSecurityException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public JwtSecurityException() {
        super();
    }
}
