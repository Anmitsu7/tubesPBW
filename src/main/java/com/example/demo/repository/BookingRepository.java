package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUsername(String username);
    List<Booking> findByFilmId(Long filmId);
    List<Booking> findByStatus(String status);
    List<Booking> findByEndDateBeforeAndStatus(LocalDate date, String status);
    
    // Untuk mendapatkan booking aktif user tertentu
    List<Booking> findByUsernameAndStatus(String username, String status);
    
    // Untuk cek apakah user sudah meminjam film tertentu
    boolean existsByUsernameAndFilmIdAndStatus(String username, Long filmId, String status);
    
    // Untuk mendapatkan total booking aktif
    long countByStatus(String status);
}