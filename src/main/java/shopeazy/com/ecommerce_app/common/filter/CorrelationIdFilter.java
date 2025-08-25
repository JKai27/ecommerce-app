package shopeazy.com.ecommerce_app.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that ensures every request has a correlation ID for tracing
 * 
 * How it works:
 * 1. Checks if request already has X-Correlation-Id header
 * 2. If not, generates a new UUID
 * 3. Adds correlation ID to response header
 * 4. Available for logging and error handling
 * 
 * Benefits:
 * - Easier debugging in production
 * - Request tracing across microservices  
 * - Better customer support (users can provide correlation ID)
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_ATTRIBUTE = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Get existing correlation ID or generate new one
        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Store in request attributes for access by other components
        httpRequest.setAttribute(CORRELATION_ID_ATTRIBUTE, correlationId);
        
        // Add to response header so client can use it for support
        httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        // Log the correlation ID for this request
        log.debug("Processing request with correlation ID: {}", correlationId);
        
        // Continue with the request
        chain.doFilter(request, response);
    }
    
    /**
     * Utility method to get correlation ID from current request
     * Can be used in services, controllers, etc.
     * 
     * Usage:
     * String correlationId = CorrelationIdFilter.getCurrentCorrelationId(request);
     */
    public static String getCurrentCorrelationId(HttpServletRequest request) {
        Object correlationId = request.getAttribute(CORRELATION_ID_ATTRIBUTE);
        return correlationId != null ? correlationId.toString() : "unknown";
    }
}