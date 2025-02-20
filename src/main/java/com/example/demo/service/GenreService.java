package com.example.demo.service;

import com.example.demo.model.Genre;
import com.example.demo.repository.GenreRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    // Metode baru untuk mendapatkan nama genre berdasarkan genreId
    public String getGenreNameById(Long genreId) {
        return genreRepository.findById(genreId)
                              .map(Genre::getNama)
                              .orElse("Unknown");
    }
}
