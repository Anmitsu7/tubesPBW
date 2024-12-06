package com.example.demo.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Optional: Add custom query methods if needed
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}