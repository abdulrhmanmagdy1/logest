package com.edham.logistics;

import com.edham.logistics.model.User;
import com.edham.logistics.model.UserRole;
import com.edham.logistics.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
public class BackendApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner setupDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("supervisor@edham.com")) {
                User user = User.builder()
                        .username("supervisor")
                        .email("supervisor@edham.com")
                        .password(passwordEncoder.encode("omnia123"))
                        .firstName("أحمد")
                        .lastName("المشرف")
                        .phoneNumber("+966500000001")
                        .role(UserRole.SUPERVISOR)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                userRepository.save(user);
                System.out.println("✅ Default supervisor user created with password: omnia123");
            } else {
                User user = userRepository.findByEmail("supervisor@edham.com").get();
                user.setPassword(passwordEncoder.encode("omnia123"));
                userRepository.save(user);
                System.out.println("✅ Supervisor password reset to: omnia123");
            }
        };
    }
}
