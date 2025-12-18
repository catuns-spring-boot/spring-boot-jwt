package xyz.catuns.spring.jwt.security.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class JwtSecurityControllerAdvice {

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ProblemDetail> handleAuthenticationException(
            AuthenticationException e, HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, e.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setTitle(e.getClass().getSimpleName());

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}
