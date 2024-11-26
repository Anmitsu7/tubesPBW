package com.rentalfilm.repository;

import com.rentalfilm.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, Long> {
    // Cari pengguna berdasarkan username
    Optional<Pengguna> findByUsername(String username);

    // Cari pengguna berdasarkan email
    Optional<Pengguna> findByEmail(String email);

    // Cari pengguna dengan role tertentu
    List<Pengguna> findByRole(Pengguna.Role role);

    // Cari pengguna yang melakukan penyewaan dalam rentang waktu tertentu
    @Query("SELECT DISTINCT p FROM Pengguna p " +
           "JOIN p.penyewaan s " +
           "WHERE s.tanggalSewa BETWEEN :startDate AND :endDate")
    List<Pengguna> findPenggunaWithRentalsBetweenDates(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
}