package com.netspecter.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Generate unique request ID for tracing
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("clientIp", getClientIP(request));

        long startTime = System.currentTimeMillis();

        try {
            logger.info("Incoming Request: {} {} from {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    getClientIP(request));

            filterChain.doFilter(request, response);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Request Completed: {} {} - Status: {} - Duration: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

        } catch (Exception e) {
            logger.error("Request Failed: {} {} - Error: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
