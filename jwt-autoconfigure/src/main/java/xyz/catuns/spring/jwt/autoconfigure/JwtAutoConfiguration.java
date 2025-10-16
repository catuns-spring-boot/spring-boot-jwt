package xyz.catuns.spring.jwt.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import xyz.catuns.spring.jwt.autoconfigure.properties.JwtProperties;

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
        log.debug("init JwtAutoConfiguration {}", properties);
    }

}
