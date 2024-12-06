package com.example.demo.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // Password encoder
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User saveUser(User user2) {
        // Check if username already exists
        if (userRepository.existsByUsername(user2.getUsername())) {
            throw new RuntimeException("Username sudah digunakan");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(user2.getEmail())) {
            throw new RuntimeException("Email sudah digunakan");
        }

        User user = new User();
        user.setUsername(user2.getUsername());
        user.setEmail(user2.getEmail());
        user.setPassword(passwordEncoder.encode(user2.getPassword()));
        
        // Set role from DTO, default to PELANGGAN if not specified
        user.setRole(user2.getRole() != null ? user2.getRole() : User.UserRole.PELANGGAN);
    
        try {
            logger.info("Saving new user: {}", user.getUsername());
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage());
            throw new RuntimeException("Gagal menyimpan pengguna", e);
        }
    }
}