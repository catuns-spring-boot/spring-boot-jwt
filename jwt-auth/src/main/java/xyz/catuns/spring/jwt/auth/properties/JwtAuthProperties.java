package xyz.catuns.spring.jwt.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jwt Authentication manager properties
 *
 * @author Devin Catuns
 * @since 1.0.0
 */
@Data
public class JwtAuthProperties {

    /**
     * Enables authentication manager
     *
     */
    private boolean enabled = true;
    /**
     * Enable use of UserEntityService
     */
    private boolean useEntityService = true;
}
