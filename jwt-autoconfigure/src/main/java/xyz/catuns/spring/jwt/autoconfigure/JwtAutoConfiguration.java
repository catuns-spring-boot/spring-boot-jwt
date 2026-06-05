package xyz.catuns.spring.jwt.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import xyz.catuns.spring.jwt.auth.AuthTokenProvider;
import xyz.catuns.spring.jwt.autoconfigure.properties.JwtProperties;
import xyz.catuns.spring.jwt.core.provider.TokenProvider;

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


    public static final String AUTH_TOKEN_PROVIDER_BEAN_NAME = "defaultAuthTokenProvider";

    @Bean(name = AUTH_TOKEN_PROVIDER_BEAN_NAME)
    @ConditionalOnMissingBean(parameterizedContainer = TokenProvider.class, value = Authentication.class)
    public TokenProvider<Authentication> defaultAuthTokenProvider() {
        return new AuthTokenProvider(properties);
    }

}
