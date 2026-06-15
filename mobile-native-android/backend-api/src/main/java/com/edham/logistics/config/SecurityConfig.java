package com.edham.logistics.config;

import com.edham.logistics.security.JwtAuthenticationFilter;
import com.edham.logistics.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration
 * Configures Spring Security with JWT authentication and role-based access control
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, 
                       JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/health").permitAll()
                .requestMatchers("/api/v1/docs/**").permitAll()
                .requestMatchers("/api/v1/swagger-ui/**").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/users/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                
                // Accountant only endpoints
                .requestMatchers("/api/v1/billing/**").hasAnyRole("ADMIN", "ACCOUNTANT", "SUPER_ADMIN")
                .requestMatchers("/api/v1/invoices/**").hasAnyRole("ADMIN", "ACCOUNTANT", "SUPER_ADMIN")
                .requestMatchers("/api/v1/payments/**").hasAnyRole("ADMIN", "ACCOUNTANT", "SUPER_ADMIN")
                
                // Driver endpoints
                .requestMatchers("/api/v1/driver/**").hasAnyRole("DRIVER", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/v1/shipments/my/**").hasAnyRole("DRIVER", "ADMIN", "SUPER_ADMIN")
                
                // Customer endpoints
                .requestMatchers("/api/v1/customer/**").hasAnyRole("CUSTOMER", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/v1/shipments/customer/**").hasAnyRole("CUSTOMER", "ADMIN", "SUPER_ADMIN")
                
                // Tracking endpoints (accessible by authenticated users)
                .requestMatchers("/api/v1/tracking/**").hasAnyRole("DRIVER", "CUSTOMER", "ADMIN", "SUPER_ADMIN")
                
                // Notification endpoints
                .requestMatchers("/api/v1/notifications/**").authenticated()
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("X-Total-Count", "X-Page-Count"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
