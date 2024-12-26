package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Film;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Map;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    // Basic queries
    List<Film> findByGenreId(Long genreId);
    List<Film> findByJudulContainingIgnoreCase(String judul);
    Optional<Film> findByJudulIgnoreCase(String judul);
    long countByStokGreaterThan(int stok);
    List<Film> findByStokLessThan(int minStok);
    
    // Advanced search queries
    @Query("""
        SELECT f FROM Film f 
        WHERE (:judul IS NULL OR LOWER(f.judul) LIKE LOWER(CONCAT('%', :judul, '%')))
        AND (:genreId IS NULL OR f.genre.id = :genreId)
        AND (:tahunRilis IS NULL OR f.tahunRilis = :tahunRilis)
        AND (:available IS NULL OR f.stok > 0)
    """)
    Page<Film> searchFilms(
        @Param("judul") String judul,
        @Param("genreId") Long genreId,
        @Param("tahunRilis") Integer tahunRilis,
        @Param("available") Boolean available,
        Pageable pageable
    );

    // Rental related queries
    @Query("""
        SELECT f FROM Film f 
        LEFT JOIN Penyewaan p ON p.film = f 
        GROUP BY f 
        ORDER BY COUNT(p) DESC
    """)
    List<Film> findMostRentedFilms(Pageable pageable);
    
    @Query("""
        SELECT COUNT(p) 
        FROM Penyewaan p 
        WHERE p.film.id = :filmId AND p.status = :status
    """)
    Long countActiveRentals(Long filmId, String status);

    // Stock management queries
    @Query("""
        SELECT f FROM Film f 
        WHERE f.stok <= :threshold 
        ORDER BY f.stok ASC
    """)
    List<Film> findLowStockFilms(@Param("threshold") int threshold);

    // Statistics queries
    @Query("""
        SELECT new map(
            f.genre.nama as genre,
            COUNT(f) as total
        )
        FROM Film f
        GROUP BY f.genre.nama
    """)
    List<Map<String, Object>> getFilmCountByGenre();

    @Query("""
        SELECT new map(
            f.tahunRilis as year,
            COUNT(f) as total
        )
        FROM Film f
        GROUP BY f.tahunRilis
        ORDER BY f.tahunRilis DESC
    """)
    List<Map<String, Object>> getFilmCountByYear();

    // Rental performance queries
    @Query("""
        SELECT f, COUNT(p) as rentalCount 
        FROM Film f 
        LEFT JOIN Penyewaan p ON p.film = f 
        WHERE p.tanggalSewa BETWEEN :startDate AND :endDate 
        GROUP BY f 
        ORDER BY rentalCount DESC
    """)
    List<Object[]> getFilmRentalStats(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Actor related queries
    @Query("""
        SELECT f FROM Film f 
        JOIN f.aktorList a 
        WHERE a.id = :aktorId
    """)
    List<Film> findByAktorId(@Param("aktorId") Long aktorId);

    // Dashboard queries
    @Query("""
        SELECT f FROM Film f 
        ORDER BY f.createdAt DESC
    """)
    List<Film> findLatestAddedFilms(Pageable pageable);

    @Query("""
        SELECT f, COUNT(p) as rentCount
        FROM Film f
        LEFT JOIN Penyewaan p ON p.film = f
        WHERE p.tanggalSewa >= :startDate
        GROUP BY f
        ORDER BY rentCount DESC
    """)
    List<Object[]> findPopularFilmsInPeriod(
        @Param("startDate") LocalDate startDate,
        Pageable pageable
    );

    // Stock alerts
    @Query("""
        SELECT f FROM Film f
        WHERE f.stok = 0
    """)
    List<Film> findOutOfStockFilms();

    @Query("""
        SELECT COUNT(f)
        FROM Film f
        WHERE f.stok = 0
    """)
    Long countOutOfStockFilms();

    // Advanced statistics
    @Query("""
        SELECT new map(
            YEAR(p.tanggalSewa) as year,
            MONTH(p.tanggalSewa) as month,
            COUNT(p) as totalRentals,
            f.judul as filmTitle
        )
        FROM Film f
        JOIN Penyewaan p ON p.film = f
        WHERE f.id = :filmId
        GROUP BY YEAR(p.tanggalSewa), MONTH(p.tanggalSewa), f.judul
        ORDER BY year DESC, month DESC
    """)
    List<Map<String, Object>> getFilmMonthlyRentalStats(@Param("filmId") Long filmId);
}