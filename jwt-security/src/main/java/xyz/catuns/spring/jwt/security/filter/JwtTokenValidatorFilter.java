package xyz.catuns.spring.jwt.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.catuns.spring.jwt.core.TokenProvider;
import xyz.catuns.spring.jwt.security.exception.JwtSecurityException;
import xyz.catuns.spring.jwt.security.exception.TokenExpiredException;

import java.io.IOException;
import java.util.function.Predicate;

import static xyz.catuns.spring.jwt.security.configurer.JwtFilterConfigurer.AUTHORIZATION_KEY;
import static xyz.catuns.spring.jwt.security.configurer.JwtFilterConfigurer.BEARER_TOKEN_PREFIX;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    private final TokenProvider<Authentication> tokenProvider;

    /**
     *
     *  Set custom header name for JWT token
     *  Default: "Authorization"
     */
    @Setter
    private String headerName = AUTHORIZATION_KEY;
    /**
     *
     *  Set custom token prefix
     *  Default: "Bearer "
     */
    @Setter
    private String tokenPrefix = BEARER_TOKEN_PREFIX;
    /**
     *
     *  Set custom predicate to determine if request requires validation
     *  Default: validates all requests with JWT token present
     */
    @Setter
    private Predicate<HttpServletRequest> requiresValidation = this::defaultRequiresValidation;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (!requiresValidation.test(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);

            if (token != null) {
                Authentication authentication = tokenProvider.validate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (log.isDebugEnabled()) {
                    log.debug("Set authentication for user: {}", authentication.getName());
                }
            }

            Authentication authentication = tokenProvider.validate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException exception) {
            log.debug("Jwt expired {}", exception.toString());
            throw new TokenExpiredException(exception);
        } catch (Exception exception) {
            throw new JwtSecurityException(exception);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from request header
     *
     * @param request HTTP request
     * @return JWT token without prefix, or null if not present
     */
    protected String extractToken(HttpServletRequest request) {
        String header = request.getHeader(headerName);

        if (!StringUtils.hasText(header)) {
            return null;
        }

        if (header.regionMatches(true, 0, tokenPrefix, 0, tokenPrefix.length())) {
            return header.substring(tokenPrefix.length()).trim();
        }

        return header.trim();
    }

    /**
     * Default logic to determine if request requires validation
     * Only validate if JWT token is present in header
     */
    private boolean defaultRequiresValidation(HttpServletRequest request) {
        String header = request.getHeader(headerName);
        return StringUtils.hasText(header);
    }

    /**
     * Skip validation if authentication already exists (e.g., from another filter)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }
}
