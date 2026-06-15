package com.edham.logistics.security;

import com.edham.logistics.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * Intercepts requests and validates JWT tokens
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt, getUserDetails(jwt))) {
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        getUserDetails(jwt), 
                        null, 
                        getUserDetails(jwt).getAuthorities()
                    );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Add security headers
                addSecurityHeaders(response);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from request
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }

    /**
     * Get user details from token
     */
    private UserDetails getUserDetails(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        return userService.loadUserByUsername(username);
    }

    /**
     * Add security headers to response
     */
    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Content-Security-Policy", "default-src 'self'");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        return path.startsWith("/api/v1/auth/") ||
               path.startsWith("/api/v1/health") ||
               path.startsWith("/api/v1/docs/") ||
               path.startsWith("/api/v1/swagger-ui/") ||
               path.equals("/api/v1/") ||
               path.startsWith("/actuator/");
    }
}
