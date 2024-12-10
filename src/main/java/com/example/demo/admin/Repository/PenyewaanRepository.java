package com.example.demo.admin.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.admin.Model.Penyewaan;

public interface PenyewaanRepository extends JpaRepository<Penyewaan, Long> {
    Long countByStatus(String status);
    List<Penyewaan> findByStatus(String status);
    List<Penyewaan> findByPenggunaId(Long userId);
    List<Penyewaan> findByFilmId(Long filmId);
    
    @Query("SELECT p FROM Penyewaan p WHERE p.status = 'DISEWA' AND p.tanggalSewa < :date")
    List<Penyewaan> findOverdueRentals(@Param("date") LocalDate date);
    
    @Query(value = "SELECT COUNT(*) FROM penyewaan WHERE EXTRACT(MONTH FROM tanggal_sewa) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM tanggal_sewa) = EXTRACT(YEAR FROM CURRENT_DATE)", nativeQuery = true)
    Long countPenyewaanBulanIni();
    
    @Query(value = "SELECT p.*, f.judul, u.username FROM penyewaan p JOIN film f ON p.film_id = f.id JOIN users u ON p.pengguna_id = u.id ORDER BY p.tanggal_sewa DESC LIMIT 5", nativeQuery = true)
    List<Map<String, Object>> findRecentRentals();
    
    @Query(value = "SELECT EXTRACT(MONTH FROM tanggal_sewa) as month, COUNT(*) as total FROM penyewaan WHERE EXTRACT(YEAR FROM tanggal_sewa) = :year GROUP BY EXTRACT(MONTH FROM tanggal_sewa) ORDER BY month", nativeQuery = true)
    List<Map<String, Object>> getMonthlyRentals(@Param("year") Integer year);
    
    @Query(value = "SELECT f.judul, COUNT(*) as total FROM penyewaan p JOIN film f ON p.film_id = f.id WHERE (:year IS NULL OR EXTRACT(YEAR FROM p.tanggal_sewa) = :year) AND (:month IS NULL OR EXTRACT(MONTH FROM p.tanggal_sewa) = :month) GROUP BY f.id, f.judul ORDER BY total DESC LIMIT 5", nativeQuery = true)
    List<Map<String, Object>> getPopularMovies(@Param("year") Integer year, @Param("month") Integer month);
    
    @Query(value = "SELECT COUNT(*) as total_rentals, COUNT(DISTINCT pengguna_id) as total_customers, COUNT(CASE WHEN status = 'DISEWA' THEN 1 END) as active_rentals FROM penyewaan WHERE (:year IS NULL OR EXTRACT(YEAR FROM tanggal_sewa) = :year) AND (:month IS NULL OR EXTRACT(MONTH FROM tanggal_sewa) = :month)", nativeQuery = true)
    Map<String, Object> getRentalSummary(@Param("year") Integer year, @Param("month") Integer month);
}