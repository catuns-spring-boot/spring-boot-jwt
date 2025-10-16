package xyz.catuns.spring.jwt.security.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import xyz.catuns.spring.jwt.security.properties.JwtSecurityProperties;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final JwtSecurityProperties properties;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper, JwtSecurityProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.properties = null;  // Use defaults
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, Object> errorDetails = buildErrorResponse(request, authException);
        objectMapper.writeValue(response.getOutputStream(), errorDetails);
    }

    private Map<String, Object> buildErrorResponse(
            HttpServletRequest request,
            AuthenticationException authException) {

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
        errorDetails.put("error", "Unauthorized");

        boolean includeMessage = properties == null || properties.getException().isIncludeMessage();
        boolean includePath = properties == null || properties.getException().isIncludePath();
        boolean includeTrace = properties != null && properties.getException().isIncludeStackTrace();

        if (includeMessage) {
            errorDetails.put("message", authException.getMessage());
        }

        if (includePath) {
            errorDetails.put("path", request.getRequestURI());
        }

        if (includeTrace) {
            errorDetails.put("trace", getStackTrace(authException));
        }

        return errorDetails;
    }

    private String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}