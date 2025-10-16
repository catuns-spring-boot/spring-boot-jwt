package xyz.catuns.spring.jwt.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import xyz.catuns.spring.jwt.auth.AuthJwtUtil;
import xyz.catuns.spring.jwt.autoconfigure.properties.JwtProperties;
import xyz.catuns.spring.jwt.core.JwtUtil;
import xyz.catuns.spring.jwt.core.exception.MissingSecretException;

/**
 * <h1>JWT Auto-Configuration</h1>
 *
 * <p>
 * Conditionally imports security configuration based on:
 * <ul>
 *      <li>Web application presence</li>
 *      <li>Spring Security on classpath</li>
 *      <li>Property settings</li>
 * </ul>
 * </p>
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfiguration {

    private final JwtProperties properties;

    public JwtAutoConfiguration(JwtProperties properties) {
        this.properties = properties;
        log.debug("Registering JwtAutoConfiguration {}", this.properties);
    }

    @Bean
    @ConditionalOnMissingBean(AuthJwtUtil.class)
    public AuthJwtUtil defaultJwtUtil() throws MissingSecretException {
        return new AuthJwtUtil(properties.getSecret(), properties.getIssuer(), properties.getExpiration());
    }

}
