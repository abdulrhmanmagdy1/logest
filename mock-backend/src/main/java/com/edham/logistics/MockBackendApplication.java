package com.edham.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class MockBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockBackendApplication.class, args);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        data.put("token", "mock-jwt-token-" + UUID.randomUUID().toString());
        data.put("refreshToken", "mock-refresh-token");
        data.put("id", 1L);
        data.put("email", "supervisor@edham.com");
        data.put("name", "Supervisor User");
        data.put("roles", Arrays.asList("ROLE_SUPERVISOR", "ROLE_ADMIN"));

        response.put("success", true);
        response.put("message", "Login successful");
        response.put("data", data);
        
        return response;
    }
}
