package com.edham.logistics.service;

import com.edham.logistics.dto.LoginResponse;
import com.edham.logistics.dto.RegisterRequest;
import com.edham.logistics.model.User;
import com.edham.logistics.model.UserRole;
import com.edham.logistics.model.UserSession;
import com.edham.logistics.repository.UserRepository;
import com.edham.logistics.repository.UserSessionRepository;
import com.edham.logistics.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponse authenticate(String email, String password, String ip, String userAgent) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword()) && !password.equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtTokenProvider.generateToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        UserSession session = new UserSession();
        session.setUser(user);
        session.setRefreshToken(refreshToken);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        userSessionRepository.save(session);

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .user(user)
            .build();
    }

    public LoginResponse register(RegisterRequest request, String ip, String userAgent) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhone());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        return authenticate(request.getEmail(), request.getPassword(), ip, userAgent);
    }

    public LoginResponse refreshToken(String refreshToken, String ip) {
        UserSession session = userSessionRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = session.getUser();
        String accessToken = jwtTokenProvider.generateToken(user.getUsername());
        
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .user(user)
            .build();
    }

    public void logout(String token, String ip) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            userSessionRepository.deleteByUserId(user.getId());
        }
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token, null);
    }

    public void forgotPassword(String email, String ip) {
        // Implement forgot password logic
    }

    public void resetPassword(String token, String newPassword, String ip) {
        // Implement reset password logic
    }

    public void changePassword(String token, String currentPassword, String newPassword, String ip) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Object getCurrentUser(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
