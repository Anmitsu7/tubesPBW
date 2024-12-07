package com.sewafilm.service;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sewafilm.model.User;
import com.sewafilm.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional  
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Pindahkan method exists ke luar saveUser
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User newUser) {
        // Validasi input
        validateUserInput(newUser);

        // Check existing username & email (sudah ada)
        if (userRepository.existsByUsername(newUser.getUsername())) {
            logger.warn("Attempt to register with existing username: {}", newUser.getUsername());
            throw new RuntimeException("Username sudah digunakan");
        }

        if (userRepository.existsByEmail(newUser.getEmail())) {
            logger.warn("Attempt to register with existing email: {}", newUser.getEmail());
            throw new RuntimeException("Email sudah digunakan");
        }

        // Create new user object
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setRole(newUser.getRole() != null ? newUser.getRole() : User.UserRole.PELANGGAN);
    
        try {
            logger.info("Saving new user: {}", user.getUsername());
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage());
            throw new RuntimeException("Gagal menyimpan pengguna: " + e.getMessage(), e);
        }
    }

    private void validateUserInput(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email tidak boleh kosong");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong");
        }

        // Validasi format email
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Format email tidak valid");
        }

        // Validasi panjang password
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // Tambahkan method untuk login
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}