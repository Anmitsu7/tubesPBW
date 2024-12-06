package com.example.demo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// UserService.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveUser(User user) {
        // Validasi username
        try{
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("Username already exists: {}", user.getUsername());
            throw new IllegalArgumentException("Username sudah digunakan");
        }

        // Validasi email
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.error("Email already exists: {}", user.getEmail());
            throw new IllegalArgumentException("Email sudah digunakan");
        }

        // Validasi role
        if (!"USER".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
            logger.error("Invalid role: {}", user.getRole());
            throw new IllegalArgumentException("Role harus USER atau ADMIN");
        }

        logger.info("Saving new user: {}", user.getUsername());
        userRepository.save(user);
        logger.info("User saved successfully: {}", user.getUsername());
    }catch(DataIntegrityViolationException e) {
        // Tangkap exception terkait constraint violation
        logger.error("Error saving user: {}", e.getMessage());
        throw new IllegalArgumentException("Terjadi kesalahan saat menyimpan pengguna baru.");
    }}

    // Metode untuk mendapatkan pengguna berdasarkan username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
