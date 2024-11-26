package com.rentalfilm.repository;

import com.rentalfilm.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    // Pencarian film berdasarkan judul (case-insensitive)
    List<Film> findByJudulContainingIgnoreCase(String judul);

    // Pencarian film berdasarkan genre
    List<Film> findByGenreNama(String namaGenre);

    // Pencarian film berdasarkan tahun rilis
    List<Film> findByTahunRilis(Integer tahun);

    // Pencarian film tersedia (stok > 0)
    List<Film> findByStokGreaterThan(Integer minStok);

    // Query kompleks untuk pencarian film berdasarkan aktor
    @Query("SELECT f FROM Film f " +
           "JOIN f.aktor a " +
           "WHERE a.nama = :namaAktor")
    List<Film> findFilmsByAktor(@Param("namaAktor") String namaAktor);

    // Pencarian film dengan stok terbatas
    @Query("SELECT f FROM Film f WHERE f.stok <= :batasStok")
    List<Film> findFilmsWithLowStock(@Param("batasStok") Integer batasStok);
}