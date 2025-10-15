package xyz.catuns.spring.jwt.domain.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jwt Entity domain properties
 */
@Data
@ConfigurationProperties(prefix = "jwt.entity")
public class JwtDomainProperties {

    private boolean enabled = true;
    private String[] packages = new String[0];
}
