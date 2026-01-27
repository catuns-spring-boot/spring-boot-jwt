package xyz.catuns.spring.jwt.domain.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Jwt Entity domain properties
 */
@Data
@ConfigurationProperties(prefix = "jwt.entity")
public class JwtDomainProperties {

    private boolean enabled = true;
    private List<String> packages = new ArrayList<>();
}
