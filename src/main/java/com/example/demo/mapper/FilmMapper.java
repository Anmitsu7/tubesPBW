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
import java.util.ArrayList;

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
        film.setId(dto.getId());
        film.setJudul(dto.getJudul());
        film.setDeskripsi(dto.getDeskripsi());
        film.setTahunRilis(dto.getTahunRilis());
        film.setStok(dto.getStok());
        film.setCoverUrl(dto.getCoverUrl());

        // Set genre
        if (dto.getGenreId() != null) {
            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new RuntimeException("Genre tidak ditemukan"));
            film.setGenre(genre);
        }

        // Set actors
        if (dto.getAktorIds() != null && !dto.getAktorIds().isEmpty()) {
            film.setActors(new HashSet<>(aktorRepository.findAllById(dto.getAktorIds())));
        }

        return film;
    }

    public FilmDTO toDto(Film film) {
        FilmDTO dto = new FilmDTO();
        dto.setId(film.getId());
        dto.setJudul(film.getJudul());
        dto.setDeskripsi(film.getDeskripsi());
        dto.setTahunRilis(film.getTahunRilis());
        dto.setStok(film.getStok());
        dto.setCoverUrl(film.getCoverUrl());
        
        // Set genre information
        if (film.getGenre() != null) {
            dto.setGenreId(film.getGenre().getId());
            dto.setGenreNama(film.getGenre().getNama());
        }

        // Set actor information
        if (film.getActors() != null) {
            dto.setAktorIds(film.getActors().stream()
                    .map(Aktor::getId)
                    .collect(Collectors.toList()));
            
            dto.setAktorNames(film.getActors().stream()
                    .map(Aktor::getNama)
                    .collect(Collectors.toList()));
        } else {
            dto.setAktorIds(new ArrayList<>());
            dto.setAktorNames(new ArrayList<>());
        }

        return dto;
    }
}