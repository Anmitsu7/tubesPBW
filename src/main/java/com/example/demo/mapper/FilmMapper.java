package com.example.demo.mapper;

import com.example.demo.dto.FilmDTO;
import com.example.demo.model.Film;
import com.example.demo.model.Genre;
import com.example.demo.model.Aktor;
import com.example.demo.repository.GenreRepository;
import com.example.demo.repository.AktorRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class FilmMapper {

    private final GenreRepository genreRepository;
    private final AktorRepository aktorRepository;

    public FilmMapper(GenreRepository genreRepository, AktorRepository aktorRepository) {
        this.genreRepository = genreRepository;
        this.aktorRepository = aktorRepository;
    }

    public Film toEntity(FilmDTO dto) {
        Film film = new Film();
        film.setJudul(dto.getJudul());
        film.setDeskripsi(dto.getDeskripsi());
        film.setTahunRilis(dto.getTahunRilis());
        film.setStok(dto.getStok());
        film.setCoverUrl(dto.getCoverUrl());

        // Set genre
        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre tidak ditemukan"));
        film.setGenre(genre);

        // Set actors
        if (dto.getAktorIds() != null && !dto.getAktorIds().isEmpty()) {
            film.setActors(new HashSet<>(aktorRepository.findAllById(dto.getAktorIds())));
        }

        return film;
    }

    public FilmDTO toDto(Film film) {
        FilmDTO dto = new FilmDTO();
        dto.setJudul(film.getJudul());
        dto.setDeskripsi(film.getDeskripsi());
        dto.setTahunRilis(film.getTahunRilis());
        dto.setStok(film.getStok());
        dto.setCoverUrl(film.getCoverUrl());
        dto.setGenreId(film.getGenre().getId());
        
        if (film.getActors() != null) {
            dto.setAktorIds(film.getActors().stream()
                    .map(Aktor::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}