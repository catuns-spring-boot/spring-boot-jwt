package xyz.catuns.spring.jwt.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import xyz.catuns.spring.jwt.auth.properties.JwtAuthProperties;
import xyz.catuns.spring.jwt.domain.properties.JwtDomainProperties;
import xyz.catuns.spring.jwt.security.properties.JwtSecurityProperties;

import java.time.Duration;

/**
 * Properties for Jwt
 *
 * @author Devin Catuns
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Enable auto configuration
     */
    private boolean enabled = true;
    /**
     * Issuer of the token
     */
    private String issuer;
    /**
     * Jwt secret key
     */
    private String secret;
    /**
     * Expiration duration of auth tokens
     */
    private Duration expiration = Duration.ofHours(10);

    /*
     * Jwt Entity Domain Properties
     */
    @NestedConfigurationProperty
    private JwtDomainProperties entity = new JwtDomainProperties();

    /*
     * Jwt Security Properties
     */
    @NestedConfigurationProperty
    private JwtSecurityProperties security = new JwtSecurityProperties();

    /*
     * Jwt Auth Properties
     */
    @NestedConfigurationProperty
    private JwtAuthProperties auth = new JwtAuthProperties();


}
