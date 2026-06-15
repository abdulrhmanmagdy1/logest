package com.edham.logistics.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiting filter to prevent brute force attacks
 * Implements sliding window rate limiting
 */
@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Value("${security.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${security.rate-limit.requests-per-hour:1000}")
    private int requestsPerHour;

    @Value("${security.rate-limit.requests-per-day:10000}")
    private int requestsPerDay;

    // Rate limit storage
    private final ConcurrentHashMap<String, ClientRequestCounter> clientCounters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);
        String endpoint = request.getRequestURI();
        
        // Skip rate limiting for health checks and public endpoints
        if (isExemptEndpoint(endpoint)) {
            filterChain.doFilter(request, response);
            return;
        }

        ClientRequestCounter counter = clientCounters.computeIfAbsent(clientIp, k -> new ClientRequestCounter());
        
        long currentTime = System.currentTimeMillis();
        
        // Check rate limits
        if (isRateLimited(counter, currentTime, response)) {
            return;
        }
        
        // Update counter
        counter.recordRequest(currentTime);
        
        // Clean up old entries
        cleanupOldEntries();
        
        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(ClientRequestCounter counter, long currentTime, 
                                HttpServletResponse response) throws IOException {
        
        // Check minute limit
        if (counter.getRequestsInLastMinute(currentTime) >= requestsPerMinute) {
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(currentTime + 60000));
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Try again later.\"}");
            log.warn("Rate limit exceeded for IP: {}", getClientIpAddress(null));
            return true;
        }
        
        // Check hour limit
        if (counter.getRequestsInLastHour(currentTime) >= requestsPerHour) {
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.setHeader("X-RateLimit-Limit-Hour", String.valueOf(requestsPerHour));
            response.setHeader("X-RateLimit-Remaining-Hour", "0");
            response.getWriter().write("{\"error\":\"Hourly rate limit exceeded.\"}");
            log.warn("Hourly rate limit exceeded for IP: {}", getClientIpAddress(null));
            return true;
        }
        
        // Check day limit
        if (counter.getRequestsInLastDay(currentTime) >= requestsPerDay) {
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.setHeader("X-RateLimit-Limit-Day", String.valueOf(requestsPerDay));
            response.setHeader("X-RateLimit-Remaining-Day", "0");
            response.getWriter().write("{\"error\":\"Daily rate limit exceeded.\"}");
            log.warn("Daily rate limit exceeded for IP: {}", getClientIpAddress(null));
            return true;
        }
        
        return false;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private boolean isExemptEndpoint(String endpoint) {
        return endpoint.startsWith("/api/v1/health") ||
               endpoint.startsWith("/api/v1/public") ||
               endpoint.startsWith("/swagger-ui") ||
               endpoint.startsWith("/v3/api-docs");
    }

    private void cleanupOldEntries() {
        long currentTime = System.currentTimeMillis();
        long oneDayAgo = currentTime - (24 * 60 * 60 * 1000);
        
        clientCounters.entrySet().removeIf(entry -> {
            ClientRequestCounter counter = entry.getValue();
            return counter.getLastRequestTime() < oneDayAgo;
        });
    }

    /**
     * Client request counter with sliding window
     */
    private static class ClientRequestCounter {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private volatile long lastRequestTime = 0;
        private final java.util.Queue<Long> requestTimes = new java.util.LinkedList<>();

        public synchronized void recordRequest(long timestamp) {
            requestTimes.offer(timestamp);
            requestCount.incrementAndGet();
            lastRequestTime = timestamp;
            
            // Keep only last 24 hours of requests
            long oneDayAgo = timestamp - (24 * 60 * 60 * 1000);
            while (!requestTimes.isEmpty() && requestTimes.peek() < oneDayAgo) {
                requestTimes.poll();
            }
        }

        public int getRequestsInLastMinute(long currentTime) {
            long oneMinuteAgo = currentTime - 60000;
            return (int) requestTimes.stream()
                    .filter(time -> time >= oneMinuteAgo)
                    .count();
        }

        public int getRequestsInLastHour(long currentTime) {
            long oneHourAgo = currentTime - (60 * 60 * 1000);
            return (int) requestTimes.stream()
                    .filter(time -> time >= oneHourAgo)
                    .count();
        }

        public int getRequestsInLastDay(long currentTime) {
            long oneDayAgo = currentTime - (24 * 60 * 60 * 1000);
            return (int) requestTimes.stream()
                    .filter(time -> time >= oneDayAgo)
                    .count();
        }

        public long getLastRequestTime() {
            return lastRequestTime;
        }
    }
}
