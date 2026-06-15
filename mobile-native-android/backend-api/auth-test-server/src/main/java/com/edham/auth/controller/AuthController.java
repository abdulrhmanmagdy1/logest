package com.edham.auth.controller;

import com.edham.auth.dto.ApiResponse;
import com.edham.auth.dto.LoginRequest;
import com.edham.auth.dto.LoginResponse;
import com.edham.auth.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    // In-memory user storage
    private final Map<String, User> users = new HashMap<>();

    public AuthController() {
        // Initialize test users
        users.put("supervisor@edham.com", new User(1L, "supervisor@edham.com", "أحمد", "المشرف", "+966500000001", "SUPERVISOR"));
        users.put("accountant@edham.com", new User(2L, "accountant@edham.com", "سارة", "المحاسبة", "+966500000002", "ACCOUNTANT"));
        users.put("driver@edham.com", new User(3L, "driver@edham.com", "خالد", "السائق", "+966500000003", "DRIVER"));
        users.put("workshop@edham.com", new User(4L, "workshop@edham.com", "محمد", "الورشة", "+966500000004", "WORKSHOP"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        User user = users.get(request.getEmail());
        
        if (user != null && "Test1234".equals(request.getPassword())) {
            // Generate fake JWT token
            String accessToken = generateFakeToken();
            String refreshToken = generateFakeToken();
            
            LoginResponse response = new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                3600L, // 1 hour
                user
            );
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
        } else {
            return ResponseEntity.status(401)
                .body(new ApiResponse<>(false, "Invalid email or password", null));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Logout successful", null));
    }

    private String generateFakeToken() {
        // Generate a fake JWT-like token (no real signing)
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + 
               System.currentTimeMillis() + 
               ".fakeSignatureForTesting";
    }
}
