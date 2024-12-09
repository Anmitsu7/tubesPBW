package com.example.demo.admin.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.admin.Model.Aktor;

public interface AktorRepository extends JpaRepository<Aktor, Long> {
    List<Aktor> findAllById(Iterable<Long> ids);
    List<Aktor> findByNamaContainingIgnoreCase(String nama);
    List<Aktor> findByNegaraAsal(String negaraAsal);
    boolean existsByNama(String nama);
}