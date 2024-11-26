package com.rentalfilm.service;

import com.rentalfilm.model.*;
import com.rentalfilm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
@Validated
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Transactional
    public Genre tambahGenre(Genre genre) {
        // Cek apakah genre sudah ada
        Optional<Genre> existingGenre = genreRepository.findByNamaIgnoreCase(genre.getNama());
        if (existingGenre.isPresent()) {
            throw new IllegalArgumentException("Genre sudah ada");
        }
        return genreRepository.save(genre);
    }

    public List<Genre> getGenreDenganFilmTerbanyak() {
        return genreRepository.findGenresOrderByFilmCount();
    }

    public List<Genre> getGenreKosong() {
        return genreRepository.findEmptyGenres();
    }
}
