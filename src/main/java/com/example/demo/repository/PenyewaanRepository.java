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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PenyewaanRepository extends JpaRepository<Penyewaan, Long> {
    Page<Penyewaan> findByPenggunaIdOrderByTanggalSewaDesc(Long penggunaId, Pageable pageable);
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
    List<Penyewaan> findTop10ByOrderByTanggalSewaDesc();
    Optional<Penyewaan> findByFilmIdAndPenggunaIdAndStatus(Long filmId, Long userId, String status);

    // Date range queries
    List<Penyewaan> findByTanggalSewaBetween(LocalDate startDate, LocalDate endDate);
    List<Penyewaan> findByTanggalKembaliBetween(LocalDate startDate, LocalDate endDate);

    // Overdue rentals
    @Query("SELECT p FROM Penyewaan p WHERE p.status = 'DISEWA' AND p.tanggalSewa < :date")
    List<Penyewaan> findOverdueRentals(@Param("date") LocalDate date);

    @Query("""
        SELECT p FROM Penyewaan p 
        WHERE p.status = 'DISEWA' 
        AND p.tanggalKembali < CURRENT_DATE
    """)
    List<Penyewaan> findAllOverdueRentals();

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
        SELECT 
            EXTRACT(MONTH FROM tanggal_sewa) as month, 
            COUNT(*) as total,
            COUNT(DISTINCT film_id) as unique_films,
            COUNT(DISTINCT pengguna_id) as unique_customers
        FROM penyewaan 
        WHERE (:year IS NULL OR EXTRACT(YEAR FROM tanggal_sewa) = :year) 
        GROUP BY EXTRACT(MONTH FROM tanggal_sewa) 
        ORDER BY month
        """, nativeQuery = true)
    List<Map<String, Object>> getMonthlyRentals(@Param("year") Integer year);

    // Daily rental statistics
    @Query(value = """
        SELECT 
            DATE(tanggal_sewa) as rental_date,
            COUNT(*) as total_rentals,
            COUNT(DISTINCT pengguna_id) as unique_customers
        FROM penyewaan
        WHERE tanggal_sewa BETWEEN :startDate AND :endDate
        GROUP BY DATE(tanggal_sewa)
        ORDER BY rental_date
        """, nativeQuery = true)
    List<Map<String, Object>> getDailyRentals(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Popular movies statistics
    @Query(value = """
        SELECT 
            f.judul as judul, 
            COUNT(*) as total,
            g.nama as genre,
            AVG(EXTRACT(DAY FROM (p.tanggal_kembali - p.tanggal_sewa))) as avg_duration
        FROM penyewaan p 
        JOIN film f ON p.film_id = f.id 
        JOIN genre g ON f.genre_id = g.id
        WHERE (:year IS NULL OR EXTRACT(YEAR FROM p.tanggal_sewa) = :year) 
        AND (:month IS NULL OR EXTRACT(MONTH FROM p.tanggal_sewa) = :month) 
        GROUP BY f.id, f.judul, g.nama
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
            COUNT(CASE WHEN status = 'DISEWA' THEN 1 END) as active_rentals,
            COUNT(CASE WHEN status = 'DIKEMBALIKAN' THEN 1 END) as completed_rentals,
            AVG(EXTRACT(DAY FROM (tanggal_kembali - tanggal_sewa))) as avg_rental_duration
        FROM penyewaan 
        WHERE (:year IS NULL OR EXTRACT(YEAR FROM tanggal_sewa) = :year)
        AND (:month IS NULL OR EXTRACT(MONTH FROM tanggal_sewa) = :month)
        """, nativeQuery = true)
    Map<String, Object> getRentalSummary(
        @Param("year") Integer year, 
        @Param("month") Integer month
    );

    // Genre popularity
    @Query(value = """
        SELECT 
            g.nama as genre,
            COUNT(*) as rental_count,
            COUNT(DISTINCT p.pengguna_id) as unique_customers
        FROM penyewaan p
        JOIN film f ON p.film_id = f.id
        JOIN genre g ON f.genre_id = g.id
        WHERE (:year IS NULL OR EXTRACT(YEAR FROM p.tanggal_sewa) = :year)
        GROUP BY g.id, g.nama
        ORDER BY rental_count DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getGenrePopularity(@Param("year") Integer year);

    // Customer rental history
    @Query(value = """
        SELECT 
            p.tanggal_sewa as tanggal_sewa,
            p.tanggal_kembali as tanggal_kembali,
            p.status as status,
            f.judul as judul_film,
            f.id as film_id,
            g.nama as genre,
            EXTRACT(DAY FROM (p.tanggal_kembali - p.tanggal_sewa)) as duration
        FROM penyewaan p
        JOIN film f ON p.film_id = f.id
        JOIN genre g ON f.genre_id = g.id
        WHERE p.pengguna_id = :userId
        ORDER BY p.tanggal_sewa DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findUserRentalHistory(@Param("userId") Long userId);

    // Active rentals pagination with search
    @Query("""
        SELECT p FROM Penyewaan p 
        WHERE p.status = 'DISEWA'
        AND (:keyword IS NULL 
             OR LOWER(p.film.judul) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.pengguna.username) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Penyewaan> findActiveRentals(
        @Param("keyword") String keyword,
        Pageable pageable
    );
    
    // Customer analytics
    @Query(value = """
        SELECT 
            u.username,
            COUNT(*) as total_rentals,
            COUNT(DISTINCT f.genre_id) as genres_rented,
            AVG(EXTRACT(DAY FROM (p.tanggal_kembali - p.tanggal_sewa))) as avg_rental_duration,
            MAX(p.tanggal_sewa) as last_rental_date
        FROM penyewaan p
        JOIN pengguna u ON p.pengguna_id = u.id
        JOIN film f ON p.film_id = f.id
        GROUP BY u.id, u.username
        ORDER BY total_rentals DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Map<String, Object>> getTopCustomers(@Param("limit") int limit);

    // Return rate analysis
    @Query(value = """
        SELECT 
            DATE(tanggal_kembali) as return_date,
            COUNT(*) as returns,
            AVG(EXTRACT(DAY FROM (tanggal_kembali - tanggal_sewa))) as avg_rental_duration
        FROM penyewaan
        WHERE status = 'DIKEMBALIKAN'
        AND tanggal_kembali BETWEEN :startDate AND :endDate
        GROUP BY DATE(tanggal_kembali)
        ORDER BY return_date
        """, nativeQuery = true)
    List<Map<String, Object>> getReturnRateAnalysis(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT p.film FROM Penyewaan p GROUP BY p.film ORDER BY COUNT(p) DESC")
    List<Film> findMostRentedFilms(Pageable pageable);

    @Query("SELECT YEAR(p.tanggalSewa) as year, COUNT(p) as count FROM Penyewaan p GROUP BY YEAR(p.tanggalSewa)")
    List<Object[]> countRentalsByYear();
}