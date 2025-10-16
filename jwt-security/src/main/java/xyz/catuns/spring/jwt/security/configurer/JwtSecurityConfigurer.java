package xyz.catuns.spring.jwt.security.configurer;

import jakarta.servlet.Filter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import xyz.catuns.spring.jwt.core.JwtUtil;
import xyz.catuns.spring.jwt.security.filter.JwtExceptionHandlerFilter;
import xyz.catuns.spring.jwt.security.filter.JwtTokenGeneratorFilter;
import xyz.catuns.spring.jwt.security.filter.JwtTokenValidatorFilter;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <h1>
 * JWT Security DSL Configurer
 * </h1>
 * <p>
 *     <ul>
 *         <li> Filter registration and positioning</li>
 *         <li> Exception handling</li>
 *         <li> Session management</li>
 *     </ul>
 * </p>
 *
 * <p>
 * <h2>Usage:</h2>
 * <pre>
 * http.with(JwtSecurityConfigurer.jwt(), jwt -> jwt<br>
 *     .filterConfigurer(myFilterConfigurer)<br>
 *     .exceptionHandling(ex -> ex.authenticationEntryPoint(customEntryPoint))
 * </pre>
 * </p>
 */
public final class JwtSecurityConfigurer extends AbstractHttpConfigurer<JwtSecurityConfigurer, HttpSecurity> {

    private JwtFilterConfigurer filterConfigurer;
    private JwtExceptionHandlingConfigurer exceptionConfigurer;

    private boolean enableValidator = true;
    private boolean enableGenerator = true;
    private boolean enableExceptionHandler = true;

    private Class<? extends Filter> validatorPosition = UsernamePasswordAuthenticationFilter.class;
    private Class<? extends Filter> generatorPosition = UsernamePasswordAuthenticationFilter.class;

    /**
     * Configure JWT service and initialize default configurers
     */
    public JwtSecurityConfigurer jwtUtil(JwtUtil<Authentication> jwtUtil) {

        // Initialize default configurers if not already set
        if (this.filterConfigurer == null) {
            this.filterConfigurer = new JwtFilterConfigurer(jwtUtil);
        } else {
            // Update existing configurer with new service
            this.filterConfigurer.setJwtUtil(jwtUtil);
        }

        if (this.exceptionConfigurer == null) {
            this.exceptionConfigurer = new JwtExceptionHandlingConfigurer();
        }

        return this;
    }
    /**
     * Inject filter configurer bean
     *
     * @param filterConfigurer custom filter configurer instance
     */
    public JwtSecurityConfigurer filterConfigurer(JwtFilterConfigurer filterConfigurer) {
        this.filterConfigurer = filterConfigurer;
        return this;
    }

    /**
     * Inject a custom filter configurer bean using a supplier
     *
     * @param filterConfigurerSupplier supplier for custom filter configurer
     */
    public JwtSecurityConfigurer filterConfigurer(Supplier<JwtFilterConfigurer> filterConfigurerSupplier) {
        this.filterConfigurer = filterConfigurerSupplier.get();
        return this;
    }

    /**
     * Inject a custom exception handling configurer bean
     *
     * @param exceptionConfigurer custom exception configurer instance
     */
    public JwtSecurityConfigurer exceptionConfigurer(JwtExceptionHandlingConfigurer exceptionConfigurer) {
        this.exceptionConfigurer = exceptionConfigurer;
        return this;
    }

    /**
     * Inject a custom exception handling configurer bean using a supplier
     *
     * @param exceptionConfigurerSupplier supplier for custom exception configurer
     */
    public JwtSecurityConfigurer exceptionConfigurer(Supplier<JwtExceptionHandlingConfigurer> exceptionConfigurerSupplier) {
        this.exceptionConfigurer = exceptionConfigurerSupplier.get();
        return this;
    }

    /**
     * Configure JWT filters (validator, generator, exception handler)
     */
    public JwtSecurityConfigurer filters(Consumer<JwtFilterConfigurer> customizer) {
        ensureInitialized();
        customizer.accept(filterConfigurer);
        return this;
    }

    /**
     * Configure exception handling (authentication entry point, access denied handler)
     */
    public JwtSecurityConfigurer exceptionHandling(Consumer<JwtExceptionHandlingConfigurer> customizer) {
        ensureInitialized();
        customizer.accept(exceptionConfigurer);
        return this;
    }
    /**
     * Disable specific filters
     */
    public JwtSecurityConfigurer disableValidator() {
        this.enableValidator = false;
        return this;
    }

    public JwtSecurityConfigurer disableGenerator() {
        this.enableGenerator = false;
        return this;
    }

    public JwtSecurityConfigurer disableExceptionHandler() {
        this.enableExceptionHandler = false;
        return this;
    }

    /**
     * Customize validator filter position
     */
    public JwtSecurityConfigurer validatorBefore(Class<? extends Filter> filterClass) {
        this.validatorPosition = filterClass;
        return this;
    }

    /**
     * Customize generator filter position
     */
    public JwtSecurityConfigurer generatorAfter(Class<? extends Filter> filterClass) {
        this.generatorPosition = filterClass;
        return this;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        ensureInitialized();

        exceptionConfigurer.initializeDefaults(http);

        // Apply exception handling to HttpSecurity
        http.exceptionHandling(exceptions -> {
            AuthenticationEntryPoint entryPoint = exceptionConfigurer.getAuthenticationEntryPoint();
            AccessDeniedHandler accessDeniedHandler = exceptionConfigurer.getAccessDeniedHandler();

            if (entryPoint != null) {
                exceptions.authenticationEntryPoint(entryPoint);
            }
            if (accessDeniedHandler != null) {
                exceptions.accessDeniedHandler(accessDeniedHandler);
            }
        });

        // Apply stateless session management
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        if (enableExceptionHandler) {
            JwtExceptionHandlerFilter exceptionFilter = filterConfigurer.buildExceptionHandler();
            http.addFilterBefore(exceptionFilter, LogoutFilter.class);
        }

        if (enableValidator) {
            JwtTokenValidatorFilter validatorFilter = filterConfigurer.buildValidator();
            http.addFilterBefore(validatorFilter, validatorPosition);
        }

        if (enableGenerator) {
            JwtTokenGeneratorFilter generatorFilter = filterConfigurer.buildGenerator();
            http.addFilterAfter(generatorFilter, generatorPosition);
        }
    }

    private void ensureInitialized() {
        if (filterConfigurer == null || exceptionConfigurer == null) {
            throw new IllegalStateException("JwtUtil must be configured first via jwtUtil() method");
        }
    }

    /**
     * Static factory method for fluent API
     */
    public static JwtSecurityConfigurer jwt() {
        return new JwtSecurityConfigurer();
    }
}