package xyz.catuns.spring.jwt.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.catuns.spring.jwt.core.model.JwtToken;
import xyz.catuns.spring.jwt.core.JwtUtil;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static xyz.catuns.spring.jwt.security.configurer.JwtFilterConfigurer.AUTHORIZATION_KEY;
import static xyz.catuns.spring.jwt.security.configurer.JwtFilterConfigurer.TOKEN_EXPIRATION_KEY;
import static xyz.catuns.spring.jwt.security.configurer.JwtFilterConfigurer.BEARER_TOKEN_PREFIX;

/**
 * JWT Token Generator Filter
 *
 * Generates JWT token after successful authentication and adds it to response headers.
 * Typically used after authentication filters (e.g., UsernamePasswordAuthenticationFilter).
 */
@Slf4j
public class JwtTokenGeneratorFilter extends OncePerRequestFilter {

    private final JwtUtil<Authentication> jwtUtil;

    /**
     * Set custom header name for JWT token
     * Default: "Authorization"
     */
    @Setter
    private String tokenHeaderName = AUTHORIZATION_KEY;
    /**
     * Set custom header name for token expiration
     * Default: "X-Token-Expiration"
     */
    @Setter
    private String expirationHeaderName = TOKEN_EXPIRATION_KEY;
    /**
     * Set custom token prefix
     * Default: "Bearer "
     */
    @Setter
    private String tokenPrefix = BEARER_TOKEN_PREFIX;
    /**
     * Set custom predicate to determine if request requires token generation
     * Default: generates for authenticated requests without existing token
     */
    @Setter
    private Predicate<HttpServletRequest> requiresGeneration = this::defaultRequiresGeneration;
    /**
     * Set custom token writer to control how token is written to response
     */
    @Setter
    private BiConsumer<HttpServletResponse, JwtToken> tokenWriter = this::defaultTokenWriter;


    public JwtTokenGeneratorFilter(JwtUtil<Authentication> jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && requiresGeneration.test(request)) {
            try {
                JwtToken jwtToken = jwtUtil.generate(authentication);
                tokenWriter.accept(response, jwtToken);

                if (log.isDebugEnabled()) {
                    log.debug("Generated JWT token for user: {}", authentication.getName());
                }
            } catch (Exception ex) {
                log.error("Failed to generate JWT token", ex);
                // Continue filter chain even if token generation fails
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Default logic to determine if request requires token generation
     * Generate token if authenticated and no token already in response
     */
    private boolean defaultRequiresGeneration(HttpServletRequest request) {
        // Only generate for specific paths (e.g., login endpoint)
        String requestURI = request.getRequestURI();
        return requestURI.contains("/login") || requestURI.contains("/auth");
    }

    /**
     * Default token writer - writes token to response headers
     */
    private void defaultTokenWriter(HttpServletResponse response, JwtToken jwtToken) {
        String tokenValue = tokenPrefix + jwtToken.value();
        response.setHeader(tokenHeaderName, tokenValue);

        if (jwtToken.expiration() != null) {
            response.setHeader(expirationHeaderName, jwtToken.expiration().toString());
        }
    }

    /**
     * Skip generation if no authentication present
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
