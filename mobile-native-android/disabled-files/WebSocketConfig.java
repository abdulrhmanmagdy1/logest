package com.edham.logistics.config;

import com.edham.logistics.security.JwtAuthenticationFilter;
import com.edham.logistics.websocket.WebSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time communication
 * Supports STOMP messaging protocol for efficient real-time updates
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketInterceptor webSocketInterceptor;

    public WebSocketConfig(WebSocketInterceptor webSocketInterceptor) {
        this.webSocketInterceptor = webSocketInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple message broker for real-time messaging
        config.enableSimpleBroker("/topic", "/queue");
        
        // Set application destination prefix
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for private messaging
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint with SockJS fallback
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // Add interceptor for authentication and authorization
        registry.addEndpoint("/ws")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Add interceptor for inbound messages
        registration.interceptors(webSocketInterceptor);
    }
}
