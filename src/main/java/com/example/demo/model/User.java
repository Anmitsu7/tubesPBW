package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList; 
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "pengguna")
public class User implements UserDetails {

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

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @NotBlank(message = "Username tidak boleh kosong")
   @Size(min = 3, max = 50, message = "Username harus antara 3 dan 50 karakter")
   @Column(nullable = false, unique = true, length = 50)
   private String username;

   @NotBlank(message = "Password tidak boleh kosong")
   @Size(min = 8, message = "Password minimal 8 karakter")
   @Column(nullable = false)
   private String password;

   @Email(message = "Format email tidak valid")
   @NotBlank(message = "Email tidak boleh kosong")
   @Column(unique = true, length = 100)
   private String email;

   @NotNull(message = "Role tidak boleh kosong")
   @Column(nullable = false, length = 10)
   @Enumerated(EnumType.STRING)
   private UserRole role;

   @Column(name = "account_non_locked")
   private boolean accountNonLocked = true;

   @Column(name = "account_non_expired")
   private boolean accountNonExpired = true;

   @Column(name = "credentials_non_expired")
   private boolean credentialsNonExpired = true;

   @Column(name = "enabled")
   private boolean enabled = true;

   @Column(name = "last_login_time")
   private LocalDateTime lastLoginTime;

   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   @Column(name = "updated_at")
   private LocalDateTime updatedAt;

   @OneToMany(mappedBy = "pengguna", cascade = CascadeType.ALL)
   private List<Penyewaan> penyewaan = new ArrayList<>();

   // Constructors
   public User() {
       this.createdAt = LocalDateTime.now();
       this.role = UserRole.PELANGGAN;
   }

   public User(String username, String password, String email, UserRole role) {
       this();
       this.username = username;
       this.password = password;
       this.email = email;
       this.role = role;
   }

   // JPA callbacks
   @PrePersist
   protected void onCreate() {
       this.createdAt = LocalDateTime.now();
       this.updatedAt = LocalDateTime.now();
   }

   @PreUpdate
   protected void onUpdate() {
       this.updatedAt = LocalDateTime.now();
   }

   // UserDetails implementation
   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
       List<GrantedAuthority> authorities = new ArrayList<>();
       authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
       return authorities;
   }

   @Override
   public String getUsername() {
       return username;
   }

   @Override
   public String getPassword() {
       return password;
   }

   @Override
   public boolean isAccountNonExpired() {
       return accountNonExpired;
   }

   @Override
   public boolean isAccountNonLocked() {
       return accountNonLocked;
   }

   @Override
   public boolean isCredentialsNonExpired() {
       return credentialsNonExpired;
   }

   @Override
   public boolean isEnabled() {
       return enabled;
   }

   // Getters and Setters
   public Long getId() {
       return id;
   }

   public void setId(Long id) {
       this.id = id;
   }

   public void setUsername(String username) {
       this.username = username;
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

   public LocalDateTime getUpdatedAt() {
       return updatedAt;
   }

   public void setUpdatedAt(LocalDateTime updatedAt) {
       this.updatedAt = updatedAt;
   }

   public List<Penyewaan> getPenyewaan() {
       return penyewaan;
   }

   public void setPenyewaan(List<Penyewaan> penyewaan) {
       this.penyewaan = penyewaan;
   }

   public void setAccountNonLocked(boolean accountNonLocked) {
       this.accountNonLocked = accountNonLocked;
   }

   public void setAccountNonExpired(boolean accountNonExpired) {
       this.accountNonExpired = accountNonExpired;
   }

   public void setCredentialsNonExpired(boolean credentialsNonExpired) {
       this.credentialsNonExpired = credentialsNonExpired;
   }

   public void setEnabled(boolean enabled) {
       this.enabled = enabled;
   }

   // Helper methods
   public boolean isAdmin() {
       return UserRole.ADMIN.equals(this.role);
   }

   public boolean isPelanggan() {
       return UserRole.PELANGGAN.equals(this.role);
   }

   public void updateLastLoginTime() {
       this.lastLoginTime = LocalDateTime.now();
   }

   @Override
   public String toString() {
       return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", role=" + role +
               ", enabled=" + enabled +
               ", lastLoginTime=" + lastLoginTime +
               '}';
   }
}