package com.rentalfilm.service;

import com.rentalfilm.model.*;
import com.rentalfilm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Validated
public class PenggunaService {
    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PenggunaService(
        PenggunaRepository penggunaRepository, 
        PasswordEncoder passwordEncoder
    ) {
        this.penggunaRepository = penggunaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Pengguna daftarPengguna(Pengguna pengguna) {
        // Validasi username dan email unik
        if (penggunaRepository.findByUsername(pengguna.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }

        if (penggunaRepository.findByEmail(pengguna.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }

        // Enkripsi password
        pengguna.setPassword(passwordEncoder.encode(pengguna.getPassword()));
        
        // Set tanggal registrasi
        pengguna.setTanggalRegistrasi(LocalDateTime.now());

        return penggunaRepository.save(pengguna);
    }

    public Pengguna loginPengguna(String username, String password) {
        Pengguna pengguna = penggunaRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Pengguna tidak ditemukan"));

        // Validasi password
        if (!passwordEncoder.matches(password, pengguna.getPassword())) {
            throw new IllegalArgumentException("Password salah");
        }

        return pengguna;
    }
}