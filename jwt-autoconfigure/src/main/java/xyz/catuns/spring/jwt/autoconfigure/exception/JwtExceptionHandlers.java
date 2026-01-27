package xyz.catuns.spring.jwt.autoconfigure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.catuns.spring.jwt.security.exception.handler.JwtSecurityControllerAdvice;

@Slf4j
@AutoConfiguration
@ConditionalOnClass({RestControllerAdvice.class, ProblemDetail.class})
@ConditionalOnWebApplication(
        type = ConditionalOnWebApplication.Type.SERVLET
)
public class JwtExceptionHandlers {

    @Bean
    @Order(10)
    public JwtSecurityControllerAdvice defaultJwtSecurityControllerAdvice() {
        log.debug("Registering JwtSecurityControllerAdvice");
        return new JwtSecurityControllerAdvice();
    }
}
