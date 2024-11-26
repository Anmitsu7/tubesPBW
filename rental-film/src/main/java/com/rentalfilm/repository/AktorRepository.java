package com.rentalfilm.repository;


import com.rentalfilm.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AktorRepository extends JpaRepository<Aktor, Long> {
    // Cari aktor berdasarkan nama (case-insensitive)
    List<Aktor> findByNamaContainingIgnoreCase(String nama);

    // Cari aktor berdasarkan negara asal
    List<Aktor> findByNegaraAsal(String negaraAsal);

    // Cari aktor yang memiliki film dalam genre tertentu
    @Query("SELECT DISTINCT a FROM Aktor a " +
           "JOIN a.films f " +
           "WHERE f.genre.nama = :namaGenre")
    List<Aktor> findAktorsByGenre(@Param("namaGenre") String namaGenre);
}