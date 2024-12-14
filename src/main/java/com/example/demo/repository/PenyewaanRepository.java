package com.example.demo.repository;

import com.example.demo.model.Film;
import com.example.demo.model.Penyewaan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface PenyewaanRepository extends JpaRepository<Penyewaan, Long> {
    // Basic queries
    Long countByStatus(String status);
    Long countByFilmIdAndStatus(Long filmId, String status);
    Long countByPenggunaId(Long userId);
    Long countByPenggunaIdAndStatus(Long userId, String status);
    
    // Find queries
    List<Penyewaan> findByStatus(String status);
    List<Penyewaan> findByPenggunaId(Long userId);
    List<Penyewaan> findByFilmId(Long filmId);
    List<Penyewaan> findByPenggunaIdOrderByTanggalSewaDesc(Long userId);

    // Overdue rentals
    @Query("SELECT p FROM Penyewaan p WHERE p.status = 'DISEWA' AND p.tanggalSewa < :date")
    List<Penyewaan> findOverdueRentals(@Param("date") LocalDate date);

    // Monthly statistics using native query
    @Query(value = """
        SELECT COUNT(*) 
        FROM penyewaan 
        WHERE EXTRACT(MONTH FROM tanggal_sewa) = EXTRACT(MONTH FROM CURRENT_DATE) 
        AND EXTRACT(YEAR FROM tanggal_sewa) = EXTRACT(YEAR FROM CURRENT_DATE)
        """, nativeQuery = true)
    Long countPenyewaanBulanIni();

    // Recent rentals with details
    @Query(value = """
        SELECT p.id, p.tanggal_sewa, p.status, f.judul, u.username 
        FROM penyewaan p 
        JOIN film f ON p.film_id = f.id 
        JOIN pengguna u ON p.pengguna_id = u.id 
        ORDER BY p.tanggal_sewa DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Map<String, Object>> findRecentRentals(@Param("limit") PageRequest limit);

    // Monthly rental statistics
    @Query(value = """
        SELECT EXTRACT(MONTH FROM tanggal_sewa) as month, COUNT(*) as total 
        FROM penyewaan 
        WHERE (:year IS NULL OR EXTRACT(YEAR FROM tanggal_sewa) = :year) 
        GROUP BY EXTRACT(MONTH FROM tanggal_sewa) 
        ORDER BY month
        """, nativeQuery = true)
    List<Map<String, Object>> getMonthlyRentals(@Param("year") Integer year);

    // Popular movies statistics
    @Query(value = """
        SELECT f.judul as judul, COUNT(*) as total 
        FROM penyewaan p 
        JOIN film f ON p.film_id = f.id 
        WHERE (:year IS NULL OR EXTRACT(YEAR FROM p.tanggal_sewa) = :year) 
        AND (:month IS NULL OR EXTRACT(MONTH FROM p.tanggal_sewa) = :month) 
        GROUP BY f.id, f.judul 
        ORDER BY COUNT(*) DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Map<String, Object>> getPopularMovies(
        @Param("year") Integer year, 
        @Param("month") Integer month,
        @Param("limit") PageRequest limit
    );

    // Rental summary
    @Query(value = """
        SELECT 
            COUNT(*) as total_rentals,
            COUNT(DISTINCT pengguna_id) as total_customers,
            COUNT(CASE WHEN status = 'DISEWA' THEN 1 END) as active_rentals
        FROM penyewaan 
        WHERE (:year IS NULL OR EXTRACT(YEAR FROM tanggal_sewa) = :year)
        AND (:month IS NULL OR EXTRACT(MONTH FROM tanggal_sewa) = :month)
        """, nativeQuery = true)
    Map<String, Object> getRentalSummary(
        @Param("year") Integer year, 
        @Param("month") Integer month
    );

    // Customer rental history
    @Query(value = """
        SELECT 
            p.tanggal_sewa as tanggal_sewa,
            p.tanggal_kembali as tanggal_kembali,
            p.status as status,
            f.judul as judul_film,
            f.id as film_id
        FROM penyewaan p
        JOIN film f ON p.film_id = f.id
        WHERE p.pengguna_id = :userId
        ORDER BY p.tanggal_sewa DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findUserRentalHistory(@Param("userId") Long userId);

    // Active rentals pagination
    @Query("SELECT p FROM Penyewaan p WHERE p.status = 'DISEWA'")
    Page<Penyewaan> findActiveRentals(Pageable pageable);
    
    // Search rentals
    @Query(value = """
        SELECT p.* FROM penyewaan p
        JOIN film f ON p.film_id = f.id
        JOIN pengguna u ON p.pengguna_id = u.id
        WHERE LOWER(f.judul) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """, nativeQuery = true)
    Page<Penyewaan> searchRentals(@Param("keyword") String keyword, Pageable pageable);

    // Existing methods
    List<Penyewaan> findTop10ByOrderByTanggalSewaDesc();
    
    // New methods for most rented films
    @Query("SELECT p.film, COUNT(p) as rentalCount FROM Penyewaan p " +
           "GROUP BY p.film " +
           "ORDER BY rentalCount DESC " +
           "LIMIT 5")
    List<Film> findMostRentedFilms();
    
    // Method untuk statistik rental per tahun
    @Query("SELECT YEAR(p.tanggalSewa) as year, COUNT(p) as count " +
           "FROM Penyewaan p " +
           "GROUP BY YEAR(p.tanggalSewa) " +
           "ORDER BY year DESC")
    List<Map<String, Object>> countRentalsByYear();
}