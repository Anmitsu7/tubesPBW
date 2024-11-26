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
public class FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final AktorRepository aktorRepository;

    @Autowired
    public FilmService(
        FilmRepository filmRepository,
        GenreRepository genreRepository,
        AktorRepository aktorRepository
    ) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.aktorRepository = aktorRepository;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public Film tambahFilm(Film film, Long genreId, List<Long> aktorIds) {
        // Validasi input
        if (film == null) {
            throw new IllegalArgumentException("Data film tidak boleh null");
        }

        // Set genre
        Genre genre = genreRepository.findById(genreId)
            .orElseThrow(() -> new EntityNotFoundException("Genre tidak ditemukan"));
        film.setGenre(genre);

        // Set aktor
        if (aktorIds != null && !aktorIds.isEmpty()) {
            List<Aktor> aktor = aktorRepository.findAllById(aktorIds);
            film.setAktor((List<Aktor>) aktor.stream().collect(java.util.stream.Collectors.toSet()));
        }

        return filmRepository.save(film);
    }

    public List<Film> cariFilmByJudul(String judul) {
        return filmRepository.findByJudulContainingIgnoreCase(judul);
    }

    public List<Film> cariFilmByGenre(String namaGenre) {
        return filmRepository.findByGenreNama(namaGenre);
    }

    public List<Film> getFilmTersedia() {
        return filmRepository.findByStokGreaterThan(0);
    }

    @Transactional
    public void kurangiStokFilm(Long filmId) {
        Film film = filmRepository.findById(filmId)
            .orElseThrow(() -> new EntityNotFoundException("Film tidak ditemukan"));
        
        if (film.getStok() > 0) {
            film.setStok(film.getStok() - 1);
            filmRepository.save(film);
        } else {
            throw new IllegalStateException("Stok film habis");
        }
    }
}