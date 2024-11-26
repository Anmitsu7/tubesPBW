package com.rentalfilm.service;

import com.rentalfilm.model.*;
import com.rentalfilm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Validated
public class AktorService {
    private final AktorRepository aktorRepository;

    @Autowired
    public AktorService(AktorRepository aktorRepository) {
        this.aktorRepository = aktorRepository;
    }

    @Transactional
    public Aktor tambahAktor(Aktor aktor) {
        // Validasi nama unik atau tambahan
        if (aktor == null) {
            throw new IllegalArgumentException("Data aktor tidak boleh null");
        }
        return aktorRepository.save(aktor);
    }

    public List<Aktor> cariAktorByNama(String nama) {
        return aktorRepository.findByNamaContainingIgnoreCase(nama);
    }

    public List<Aktor> getAktorByGenre(String namaGenre) {
        return aktorRepository.findAktorsByGenre(namaGenre);
    }

    @Transactional
    public void hapusAktor(Long id) {
        Aktor aktor = aktorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aktor tidak ditemukan"));
        aktorRepository.delete(aktor);
    }
}