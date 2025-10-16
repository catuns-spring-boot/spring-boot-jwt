package xyz.catuns.spring.jwt.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import xyz.catuns.spring.jwt.security.exception.JwtSecurityException;

import java.io.IOException;

/**
 * Exception Handler Filter
 *
 * Catches exceptions thrown in the filter chain and delegates them to Spring MVC
 * exception handling mechanism for consistent error responses.
 *
 * Should be placed early in the filter chain (before other JWT filters).
 */
@Slf4j
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver resolver;
    /**
     * Enable/disable exception logging
     * Default: true
     */
    @Setter
    private boolean logExceptions = true;

    public JwtExceptionHandlerFilter(HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtSecurityException ex) {
            logException("JWT security exception", ex);
            resolver.resolveException(request, response, null, ex);
        } catch (AuthenticationException ex) {
            logException("Authentication exception", ex);
            resolver.resolveException(request, response, null, ex);
        } catch (AccessDeniedException ex) {
            logException("Access denied exception", ex);
            resolver.resolveException(request, response, null, ex);
        } catch (ServletException | IOException ex) {
            // Re-throw servlet/IO exceptions as they should be handled by container
            throw ex;
        } catch (Exception ex) {
            logException("Unexpected exception in filter chain", ex);
            resolver.resolveException(request, response, null, ex);
        }
    }

    private void logException(String message, Exception ex) {
        if (logExceptions) {
            if (log.isDebugEnabled()) {
                log.debug("{}: {}", message, ex.getMessage(), ex);
            } else {
                log.warn("{}: {}", message, ex.getMessage());
            }
        }
    }
}
