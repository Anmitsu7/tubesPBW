package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.demo.model.Aktor;

public interface AktorRepository extends JpaRepository<Aktor, Long> {
    List<Aktor> findAllById(Iterable<Long> ids);
    List<Aktor> findByNamaContainingIgnoreCase(String nama);
    List<Aktor> findByNegaraAsal(String negaraAsal);
    boolean existsByNama(String nama);

    @Query("SELECT DISTINCT a FROM Aktor a LEFT JOIN FETCH a.films")
    List<Aktor> findAllWithFilms();
    
    @Query("SELECT a FROM Aktor a LEFT JOIN FETCH a.films ORDER BY a.nama")
    List<Aktor> findAllWithFilmsOrderByNama();
}