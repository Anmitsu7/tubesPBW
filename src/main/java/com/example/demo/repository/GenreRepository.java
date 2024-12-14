package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    long count();
    Optional<Genre> findByNama(String nama);
    boolean existsByNama(String nama);
    List<Genre> findByNamaContainingIgnoreCase(String nama);
    
    @Query("SELECT g.nama, COUNT(f) FROM Genre g LEFT JOIN Film f ON f.genre = g GROUP BY g.id, g.nama")
    List<Object[]> getGenreStatistics();
}