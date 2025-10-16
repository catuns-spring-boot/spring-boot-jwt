package xyz.catuns.spring.jwt.security.configurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import xyz.catuns.spring.jwt.security.properties.JwtSecurityProperties;
import xyz.catuns.spring.jwt.security.exception.handler.JwtAccessDeniedHandler;
import xyz.catuns.spring.jwt.security.exception.handler.JwtAuthenticationEntryPoint;

public class JwtExceptionHandlingConfigurer {
    private AuthenticationEntryPoint authenticationEntryPoint;
    private AccessDeniedHandler accessDeniedHandler;
    private ObjectMapper objectMapper;
    private JwtSecurityProperties properties;

    /**
     * Configure custom authentication entry point
     */
    public JwtExceptionHandlingConfigurer authenticationEntryPoint(AuthenticationEntryPoint entryPoint) {
        this.authenticationEntryPoint = entryPoint;
        return this;
    }

    /**
     * Configure custom access denied handler
     */
    public JwtExceptionHandlingConfigurer accessDeniedHandler(AccessDeniedHandler handler) {
        this.accessDeniedHandler = handler;
        return this;
    }

    /**
     * Configure ObjectMapper for JSON error responses
     */
    public JwtExceptionHandlingConfigurer objectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    /**
     * Configure properties for exception response customization
     */
    public JwtExceptionHandlingConfigurer properties(JwtSecurityProperties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Initialize default handlers if not explicitly set
     */
    void initializeDefaults(HttpSecurity http) {
        if (objectMapper == null) {
            objectMapper = http.getSharedObject(ObjectMapper.class);
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }
        }

        if (authenticationEntryPoint == null) {
            authenticationEntryPoint = properties != null
                    ? new JwtAuthenticationEntryPoint(objectMapper, properties)
                    : new JwtAuthenticationEntryPoint(objectMapper);
        }

        if (accessDeniedHandler == null) {
            accessDeniedHandler = properties != null
                    ? new JwtAccessDeniedHandler(objectMapper, properties)
                    : new JwtAccessDeniedHandler(objectMapper);
        }
    }

    AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }

    AccessDeniedHandler getAccessDeniedHandler() {
        return accessDeniedHandler;
    }
}
