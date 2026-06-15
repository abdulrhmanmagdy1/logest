package com.edham.logistics.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Enhanced security configuration for comprehensive system protection
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                        CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf().disable()
            
            // Configure CORS
            .cors().configurationSource(corsConfigurationSource())
            
            // Configure session management
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            // Configure authorization
            .and()
            .authorizeRequests()
            // Public endpoints
            .antMatchers("/api/v1/auth/**").permitAll()
            .antMatchers("/api/v1/health/**").permitAll()
            .antMatchers("/api/v1/public/**").permitAll()
            .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            
            // Admin only endpoints
            .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .antMatchers("/api/v1/system/**").hasRole("ADMIN")
            
            // Supervisor and admin endpoints
            .antMatchers("/api/v1/supervisor/**").hasAnyRole("SUPERVISOR", "ADMIN")
            .antMatchers("/api/v1/reports/**").hasAnyRole("SUPERVISOR", "ADMIN")
            .antMatchers("/api/v1/maintenance/**").hasAnyRole("SUPERVISOR", "ADMIN", "WORKSHOP")
            
            // Driver endpoints
            .antMatchers("/api/v1/driver/**").hasRole("DRIVER")
            .antMatchers("/api/v1/my-shipments/**").hasRole("DRIVER")
            
            // Customer endpoints
            .antMatchers("/api/v1/customer/**").hasRole("CUSTOMER")
            .antMatchers("/api/v1/my-orders/**").hasRole("CUSTOMER")
            
            // Workshop endpoints
            .antMatchers("/api/v1/workshop/**").hasRole("WORKSHOP")
            
            // Accountant endpoints
            .antMatchers("/api/v1/accounting/**").hasRole("ACCOUNTANT")
            
            // All other requests require authentication
            .anyRequest().authenticated()
            
            // Configure exception handling
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler)
            
            // Add JWT filter
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configure allowed origins (restrictive for production)
        configuration.setAllowedOriginPatterns(List.of("https://*.edham.com", "https://edham.com"));
        
        // Configure allowed methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Configure allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"
        ));
        
        // Configure exposed headers
        configuration.setExposedHeaders(Arrays.asList("X-Total-Count", "X-Page-Count"));
        
        // Configure credentials
        configuration.setAllowCredentials(true);
        
        // Configure max age
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strong encryption
    }

    @Bean
    public RateLimitingFilter rateLimitingFilter() {
        return new RateLimitingFilter();
    }

    @Bean
    public SecurityHeadersFilter securityHeadersFilter() {
        return new SecurityHeadersFilter();
    }
}
