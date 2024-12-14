package com.example.demo.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Film;
import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    List<Film> findByGenreId(Long genreId);
    
    List<Film> findByJudulContainingIgnoreCase(String judul);
    
    long countByStokGreaterThan(int stok);
    
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
}