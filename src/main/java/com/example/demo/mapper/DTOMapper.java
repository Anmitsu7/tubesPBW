package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class DTOMapper {
    
    // Genre Mappings
    public GenreDTO toGenreDTO(Genre genre) {
        GenreDTO dto = new GenreDTO();
        dto.setId(genre.getId());
        dto.setNama(genre.getNama());
        // Set additional fields if needed
        return dto;
    }

    public Genre toGenreEntity(GenreDTO dto) {
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setNama(dto.getNama());
        return genre;
    }

    // Aktor Mappings
    public AktorDTO toAktorDTO(Aktor aktor) {
        AktorDTO dto = new AktorDTO();
        dto.setId(aktor.getId());
        dto.setNama(aktor.getNama());
        dto.setNegaraAsal(aktor.getNegaraAsal());
        dto.setFotoUrl(aktor.getFotoUrl());
        
        if (aktor.getFilms() != null) {
            dto.setFilmList(aktor.getFilms().stream()
                .map(Film::getJudul)
                .collect(Collectors.toList()));
            dto.setTotalFilms(aktor.getFilms().size());
        }
        
        return dto;
    }

    public Aktor toAktorEntity(AktorDTO dto) {
        Aktor aktor = new Aktor();
        aktor.setId(dto.getId());
        aktor.setNama(dto.getNama());
        aktor.setNegaraAsal(dto.getNegaraAsal());
        aktor.setFotoUrl(dto.getFotoUrl());
        return aktor;
    }

    // Film Mappings
    public FilmDTO toFilmDTO(Film film) {
        FilmDTO dto = new FilmDTO();
        dto.setId(film.getId());
        dto.setJudul(film.getJudul());
        dto.setDeskripsi(film.getDeskripsi());
        dto.setTahunRilis(film.getTahunRilis());
        dto.setGenreId(film.getGenre().getId());
        dto.setGenreNama(film.getGenre().getNama());
        dto.setStok(film.getStok());
        dto.setCoverUrl(film.getCoverUrl());
        
        if (film.getActors() != null) {
            dto.setAktorIds(film.getActors().stream()
                .map(Aktor::getId)
                .collect(Collectors.toList()));
            dto.setAktorNames(film.getActors().stream()
                .map(Aktor::getNama)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    public Film toFilmEntity(FilmDTO dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setJudul(dto.getJudul());
        film.setDeskripsi(dto.getDeskripsi());
        film.setTahunRilis(dto.getTahunRilis());
        film.setStok(dto.getStok());
        film.setCoverUrl(dto.getCoverUrl());
        return film;
    }
}