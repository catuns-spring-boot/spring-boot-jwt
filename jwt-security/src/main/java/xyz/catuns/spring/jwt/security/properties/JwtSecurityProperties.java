package xyz.catuns.spring.jwt.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static xyz.catuns.spring.jwt.security.configurer.JwtFilterConfigurer.AUTHORIZATION_KEY;
import static xyz.catuns.spring.jwt.security.configurer.JwtFilterConfigurer.TOKEN_EXPIRATION_KEY;
import static xyz.catuns.spring.jwt.security.configurer.JwtFilterConfigurer.BEARER_TOKEN_PREFIX;


/**
 *
 *
 * @author Devin Catuns
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "jwt.security")
public class JwtSecurityProperties {

    public static final int DEFAULT_FILTER_ORDER = -100;

    /**
     * Enables JWT security autoconfiguration
     */
    private boolean enabled = true;
    /**
     * Public paths that don't require authentication
     */
    private String[] publicPaths = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/health/**",
            "/error/**",
    };

    /**
     * Enable/disable JWT filters
     */
    private FilterConfig filter = new FilterConfig();

    /**
     * CORS configuration
     */
    private CorsConfig cors = new CorsConfig();

    /**
     * Exception handling configuration
     */
    private ExceptionConfig exception = new ExceptionConfig();

    /**
     * Token validation filter configuration
     */
    private ValidationConfig validation = new ValidationConfig();

    /**
     * Token generation filter configuration
     */
    private GenerationConfig generation = new GenerationConfig();

    @Data
    public static class FilterConfig {
        private boolean validator = true;
        private boolean generator = true;
        private boolean exceptionHandler = true;
        private boolean enabled = true;
        private int order = DEFAULT_FILTER_ORDER;
    }

    @Data
    public static class CorsConfig {
        /**
         * Enable CORS
         */
        private boolean enabled = true;
        /**
         * Allowed origins
         */
        private String[] allowedOrigins = {};
        /**
         * Allowed methods
         */
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"};
        /**
         * Allowed headers
         */
        private String[] allowedHeaders = {};
        /**
         * Allow credentials
         */
        private boolean allowCredentials = true;
        /**
         * Max age
         */
        private long maxAge = 3600;
    }

    @Data
    public static class ExceptionConfig {
        /**
         * Include exception message in response
         */
        private boolean includeMessage = true;

        /**
         * Include stack trace in response (only for development)
         */
        private boolean includeStackTrace = false;

        /**
         * Include request path in error response
         */
        private boolean includePath = true;
    }


    @Data
    public static class ValidationConfig {
        /**
         * Header name for JWT token validation
         */
        private String headerName = AUTHORIZATION_KEY;

        /**
         * Token prefix for validation
         */
        private String tokenPrefix = BEARER_TOKEN_PREFIX;
    }

    @Data
    public static class GenerationConfig {
        /**
         * Header name for JWT token generation
         */
        private String headerName = AUTHORIZATION_KEY;

        /**
         * Token prefix for generation
         */
        private String tokenPrefix = BEARER_TOKEN_PREFIX;

        /**
         * Header name for token expiration
         */
        private String expirationHeaderName = TOKEN_EXPIRATION_KEY;
    }
}