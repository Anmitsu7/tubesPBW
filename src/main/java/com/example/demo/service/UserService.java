package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.model.Penyewaan;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.PenyewaanRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan: " + username));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        
        // Update last login time
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                Collections.singletonList(authority));
    }

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PenyewaanRepository penyewaanRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public User registerUser(RegisterRequest request) {
        try {
            logger.info("Starting registration process for user: {}", request.getUsername());
            
            // Log validasi
            logger.info("Validating registration data");
            validateRegistration(request);
            
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setRole(User.UserRole.PELANGGAN);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setEnabled(true);
            newUser.setAccountNonLocked(true);
            newUser.setAccountNonExpired(true);
            newUser.setCredentialsNonExpired(true);

            logger.info("Saving user to database");
            User savedUser = userRepository.save(newUser);
            logger.info("User saved successfully with ID: {}", savedUser.getId());

            return savedUser;
        } catch (Exception e) {
            logger.error("Error in registerUser: {}", e.getMessage(), e);
            throw new RuntimeException("Terjadi kesalahan saat menyimpan pengguna baru: " + e.getMessage());
        }
    }

    private void validateRegistration(RegisterRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            errors.add("Username tidak boleh kosong");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.add("Email tidak boleh kosong");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            errors.add("Password tidak boleh kosong");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            errors.add("Username sudah digunakan");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            errors.add("Email sudah digunakan");
        }

        if (request.getPassword() != null && !request.getPassword().equals(request.getPasswordConfirm())) {
            errors.add("Password tidak cocok");
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join(", ", errors));
        }
    }

    // Existing authentication method
    public Optional<Map<String, Object>> authenticateUser(LoginRequest loginRequest) {
        try {
            logger.info("Authenticating user: {}", loginRequest.getUsername());

            // Find user
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

            if (!userOpt.isPresent()) {
                logger.warn("User not found: {}", loginRequest.getUsername());
                return Optional.empty();
            }

            User user = userOpt.get();

            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.warn("Invalid password for user: {}", loginRequest.getUsername());
                return Optional.empty();
            }

            // Create response data
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("role", user.getRole());
            userData.put("email", user.getEmail());

            // Update last login time
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);

            logger.info("Authentication successful for user: {}", user.getUsername());
            return Optional.of(userData);

        } catch (Exception e) {
            logger.error("Authentication error for user: {}, error: {}",
                    loginRequest.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Gagal melakukan autentikasi", e);
        }
    }

    // Helper methods untuk validasi
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // User Management
    @Transactional
    public User saveUser(User user) {
        try {
            validateNewUser(user);

            User newUser = new User();
            newUser.setUsername(user.getUsername());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            // Explicitly check for null role before setting
            if (user.getRole() == null) {
                newUser.setRole(User.UserRole.PELANGGAN);
                logger.info("Setting default role PELANGGAN for user: {}", user.getUsername());
            } else {
                newUser.setRole(user.getRole());
            }
            newUser.setCreatedAt(LocalDateTime.now()); // Add creation timestamp

            logger.info("Attempting to save user: {}", user.getUsername());
            return userRepository.save(newUser);
        } catch (Exception e) {
            logger.error("Detailed error saving user: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal menyimpan pengguna: " + e.getMessage(), e);
        }
    }

    private void validateNewUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("Username already exists: {}", user.getUsername());
            throw new RuntimeException("Username sudah digunakan");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Email already exists: {}", user.getEmail());
            throw new RuntimeException("Email sudah digunakan");
        }
    }

    // User Profile
    public Map<String, Object> getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole());
        profile.put("createdAt", user.getCreatedAt());
        profile.put("lastLoginTime", user.getLastLoginTime());

        // Add rental statistics
        profile.put("totalRentals", penyewaanRepository.countByPenggunaId(user.getId()));
        profile.put("activeRentals", penyewaanRepository.countByPenggunaIdAndStatus(user.getId(), "DISEWA"));

        return profile;
    }

    public Map<String, Object> getUserData(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());

        return userData;
    }

    // Rental History
    public List<Map<String, Object>> getUserRentalHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        List<Penyewaan> rentals = penyewaanRepository.findByPenggunaIdOrderByTanggalSewaDesc(user.getId());
        List<Map<String, Object>> history = new ArrayList<>();

        for (Penyewaan rental : rentals) {
            Map<String, Object> rentalData = new HashMap<>();
            rentalData.put("filmTitle", rental.getFilm().getJudul());
            rentalData.put("rentalDate", rental.getTanggalSewa());
            rentalData.put("returnDate", rental.getTanggalKembali());
            rentalData.put("status", rental.getStatus());
            history.add(rentalData);
        }

        return history;
    }

    // Password Management
    @Transactional
    public void updatePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Password lama tidak sesuai");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Profile Update
    @Transactional
    public User updateUserProfile(String username, User updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (updatedUser.getEmail() != null &&
                !updatedUser.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email sudah digunakan");
        }

        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
    }

    public long getTotalActiveUsers() {
        return userRepository.countByLastLoginTimeIsNotNull();
    }
    
    public long getTotalUsers() {
        return userRepository.count();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User createAdminUser(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username sudah digunakan");
        }

        User adminUser = new User();
        adminUser.setUsername(username);
        adminUser.setPassword(passwordEncoder.encode(password));
        adminUser.setEmail(email);
        adminUser.setRole(User.UserRole.ADMIN);
        adminUser.setEnabled(true);
        adminUser.setCreatedAt(LocalDateTime.now());

        return userRepository.save(adminUser);
    }

    @Transactional
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    @Transactional
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }

    // Method untuk validasi password
    public boolean isValidPassword(String password) {
        return password != null &&
               password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&    // at least one uppercase
               password.matches(".*[a-z].*") &&    // at least one lowercase
               password.matches(".*\\d.*");        // at least one digit
    }

    // Method untuk reset password
    @Transactional
    public void resetPassword(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        
        String temporaryPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setCredentialsNonExpired(false);
        
        userRepository.save(user);
        
        // TODO: Send email with temporary password
        logger.info("Password reset for user: {}", username);
    }

    private String generateTemporaryPassword() {
        // Generate a random 10-character password
        return UUID.randomUUID().toString().substring(0, 10);
    }
}