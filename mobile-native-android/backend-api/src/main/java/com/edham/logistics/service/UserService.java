package com.edham.logistics.service;

import com.edham.logistics.dto.UserRequest;
import com.edham.logistics.dto.UserResponse;
import com.edham.logistics.dto.UserUpdateRequest;
import com.edham.logistics.model.User;
import com.edham.logistics.model.UserRole;
import com.edham.logistics.repository.UserRepository;
import com.edham.logistics.util.PaginatedResponse;
import com.edham.logistics.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogger auditLogger;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public PaginatedResponse<UserResponse> getAllUsers(Pageable pageable, String role, String query, String status) {
        Page<User> users = userRepository.findAllWithFilters(pageable, role, query, status);
        List<UserResponse> responses = users.getContent().stream().map(this::convertToResponse).collect(Collectors.toList());
        return new PaginatedResponse<>(responses, users.getNumber(), users.getSize(), users.getTotalElements(), users.getTotalPages(), users.isLast());
    }

    public UserResponse getUserById(Long id) {
        return convertToResponse(userRepository.findById(id).orElseThrow());
    }

    public UserResponse createUser(UserRequest request, HttpServletRequest httpRequest) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhone());
        user.setRole(UserRole.valueOf(request.getRoles().iterator().next()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return convertToResponse(userRepository.save(user));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findById(id).orElseThrow();
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        user.setUpdatedAt(LocalDateTime.now());
        return convertToResponse(userRepository.save(user));
    }

    public void deleteUser(Long id, HttpServletRequest httpRequest) {
        userRepository.deleteById(id);
    }

    public UserResponse getCurrentUser(HttpServletRequest request) {
        return convertToResponse((User) loadUserByUsername("admin")); // Placeholder
    }

    public UserResponse updateProfile(UserUpdateRequest request, HttpServletRequest httpRequest) {
        return updateUser(1L, request, httpRequest); // Placeholder
    }

    public UserResponse changeUserStatus(Long id, boolean active, HttpServletRequest request) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(active);
        return convertToResponse(userRepository.save(user));
    }

    public UserResponse changeUserRole(Long id, String role, HttpServletRequest request) {
        User user = userRepository.findById(id).orElseThrow();
        user.setRole(UserRole.valueOf(role));
        return convertToResponse(userRepository.save(user));
    }

    public PaginatedResponse<UserResponse> getUsersByRole(String role, Pageable pageable) {
        return getAllUsers(pageable, role, null, null);
    }

    public PaginatedResponse<UserResponse> getUsersByOrganization(Long orgId, Pageable pageable) {
        return getAllUsers(pageable, null, null, null);
    }

    public PaginatedResponse<UserResponse> searchUsers(String query, String status, Pageable pageable) {
        return getAllUsers(pageable, null, query, status);
    }

    public Object getUserStatistics() { return null; }

    public PaginatedResponse<Object> getUserActivity(Long id, Pageable pageable) { return null; }

    public void resetUserPassword(Long id, HttpServletRequest request) {}

    public byte[] exportUsersToCsv(String role, String status) { return new byte[0]; }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhone(user.getPhoneNumber());
        response.setStatus(user.getActive() ? "ACTIVE" : "INACTIVE");
        if (user.getRole() != null) response.setRoles(Set.of(user.getRole().name()));
        return response;
    }
}
