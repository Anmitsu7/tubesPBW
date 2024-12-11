package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    Long countByRole(String role);
    
    @Query("SELECT u FROM User u WHERE u.lastLoginTime IS NOT NULL ORDER BY u.lastLoginTime DESC LIMIT 10")
    List<User> findRecentLogins();
    
    @Query("""
        SELECT u FROM User u 
        WHERE EXISTS (
            SELECT p FROM Penyewaan p 
            WHERE p.pengguna = u AND p.status = 'DISEWA'
        )
    """)
    List<User> findActiveUsers();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);

    long countByLastLoginTimeIsNotNull();
}