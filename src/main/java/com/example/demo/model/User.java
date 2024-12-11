package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pengguna")
public class User {

    public enum UserRole {
        ADMIN("ADMIN"),
        PELANGGAN("PELANGGAN");

        private final String value;

        UserRole(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public User() {
        this.createdAt = LocalDateTime.now();
        this.role = UserRole.PELANGGAN; // Set default role
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 50, message = "Username harus antara 3 dan 50 karakter")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    @Column(nullable = false)
    private String password;

    @Email(message = "Format email tidak valid")
    @Column(unique = true, length = 100)
    private String email;

    @NotNull(message = "Role tidak boleh kosong")
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private UserRole role;


    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "pengguna", cascade = CascadeType.ALL)
    private List<Penyewaan> penyewaan;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


    public User(String username, String password, String email, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Penyewaan> getPenyewaan() {
        return penyewaan;
    }

    public void setPenyewaan(List<Penyewaan> penyewaan) {
        this.penyewaan = penyewaan;
    }

    // Helper methods
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }

    public boolean isPelanggan() {
        return UserRole.PELANGGAN.equals(this.role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}