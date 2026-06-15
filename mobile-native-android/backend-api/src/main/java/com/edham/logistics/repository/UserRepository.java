package com.edham.logistics.repository;

import com.edham.logistics.model.User;
import com.edham.logistics.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByActiveTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:query IS NULL OR u.username LIKE %:query% OR u.email LIKE %:query% OR u.firstName LIKE %:query% OR u.lastName LIKE %:query%)")
    Page<User> findAllWithFilters(Pageable pageable, String role, String query);
    
    // Default implementation to match Service signature if needed
    default Page<User> findAllWithFilters(Pageable pageable, String role, String query, String status) {
        return findAllWithFilters(pageable, role, query);
    }

    long countByRole(UserRole role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginAt >= :date")
    long countByLastLoginAfter(LocalDateTime date);
}
