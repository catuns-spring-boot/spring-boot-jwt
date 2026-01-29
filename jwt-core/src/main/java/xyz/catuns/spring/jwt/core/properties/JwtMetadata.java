package xyz.catuns.spring.jwt.core.properties;

import java.time.Duration;

/**
 * Properties for Jwt
 *
 * @author Devin Catuns
 * @since 1.0.0
 */
public class JwtMetadata {

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

    
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getIssuer() {
        return issuer;
    }
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }
    public Duration getExpiration() {
        return expiration;
    }
    public void setExpiration(Duration expiration) {
        this.expiration = expiration;
    }

    

}
