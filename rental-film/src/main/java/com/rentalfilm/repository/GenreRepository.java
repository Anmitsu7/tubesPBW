package com.rentalfilm.repository;

import com.rentalfilm.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    // Cari genre berdasarkan nama (case-insensitive)
    Optional<Genre> findByNamaIgnoreCase(String nama);

    // Cari genre dengan jumlah film terbanyak
    @Query("SELECT g FROM Genre g ORDER BY SIZE(g.films) DESC")
    List<Genre> findGenresOrderByFilmCount();

    // Cari genre yang tidak memiliki film
    @Query("SELECT g FROM Genre g WHERE g.films IS EMPTY")
    List<Genre> findEmptyGenres();
}