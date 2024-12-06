package com.example.demo.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Metode untuk memeriksa keberadaan username
    boolean existsByUsername(String username);

    // Metode untuk memeriksa keberadaan email
    boolean existsByEmail(String email);

    // Metode untuk mencari pengguna berdasarkan username (opsional)
    Optional<User> findByUsername(String username);
}
