package com.example.demo.admin.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.admin.Model.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {
    long count();
    List<Film> findByJudulContainingIgnoreCase(String judul);
    List<Film> findByGenreId(Long genreId);
    List<Film> findByTahunRilis(Integer tahunRilis);
    List<Film> findByStokGreaterThan(Integer stok);
    List<Film> findByActorsId(Long aktorId);
    
    @Query("SELECT f FROM Film f WHERE f.stok = 0")
    List<Film> findOutOfStockFilms();
    
    @Query("SELECT f FROM Film f WHERE f.stok > 0")
    List<Film> findAvailableFilms();
}