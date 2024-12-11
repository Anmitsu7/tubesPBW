package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.example.demo.dto.FilmDTO;
import com.example.demo.mapper.FilmMapper;
import com.example.demo.model.Film;
import com.example.demo.model.Genre;
import com.example.demo.repository.FilmRepository;
import com.example.demo.repository.GenreRepository;
import com.example.demo.repository.PenyewaanRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

@Service
public class FilmService {
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);
    
    @Autowired
    private FilmMapper filmMapper;
    
    @Autowired
    private FilmRepository filmRepository;
    
    @Autowired
    private GenreRepository genreRepository;
    
    @Autowired
    private PenyewaanRepository penyewaanRepository;
    
    @Value("${file.upload.directory}")
    private String uploadDirectory;

    @Transactional
    public Film createFilm(FilmDTO filmDTO, MultipartFile coverFile) {
        try {
            String fileName = StringUtils.cleanPath(coverFile.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            
            // Save file
            Path uploadPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(coverFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create film with cover URL
            Film film = filmMapper.toEntity(filmDTO);
            film.setCoverUrl("/covers/" + uniqueFileName);
            
            return filmRepository.save(film);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengupload cover film", e);
        }
    }
    
    public FilmDTO getFilmDto(Long id) {
        Film film = filmRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Film not found with id: {}", id);
                return new RuntimeException("Film tidak ditemukan");
            });
        return filmMapper.toDto(film);
    }
    
    @Transactional
    public Film updateFilm(Long id, FilmDTO filmDTO) {
        try {
            Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film tidak ditemukan"));
            
            Film updatedFilm = filmMapper.toEntity(filmDTO);
            updatedFilm.setId(existingFilm.getId());
            
            logger.info("Updating film with id: {}", id);
            return filmRepository.save(updatedFilm);
        } catch (Exception e) {
            logger.error("Error updating film: {}", e.getMessage());
            throw new RuntimeException("Gagal mengupdate film", e);
        }
    }
    
    @Transactional
    public void deleteFilm(Long id) {
        try {
            logger.info("Deleting film with id: {}", id);
            filmRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting film: {}", e.getMessage());
            throw new RuntimeException("Gagal menghapus film", e);
        }
    }
    
    // Query Operations
    public List<FilmDTO> getAllFilms() {
        return filmRepository.findAll().stream()
            .map(filmMapper::toDto)
            .collect(Collectors.toList());
    }
    
    public List<FilmDTO> getLatestFilms() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("tahunRilis").descending());
        Page<Film> latestFilms = filmRepository.findAll(pageable);
        return latestFilms.getContent().stream()
            .map(filmMapper::toDto)
            .collect(Collectors.toList());
    }
    
    public List<FilmDTO> getPopularFilms() {
        return filmRepository.findMostRentedFilms(PageRequest.of(0, 10))
            .stream()
            .map(filmMapper::toDto)
            .collect(Collectors.toList());
    }
    
    public List<FilmDTO> getFilmsByGenre(Long genreId) {
        return filmRepository.findByGenreId(genreId).stream()
            .map(filmMapper::toDto)
            .collect(Collectors.toList());
    }
    
    public List<FilmDTO> searchFilms(String keyword) {
        return filmRepository.findByJudulContainingIgnoreCase(keyword).stream()
            .map(filmMapper::toDto)
            .collect(Collectors.toList());
    }

    // Availability Management
    public boolean isFilmAvailable(Long filmId) {
        Film film = filmRepository.findById(filmId)
            .orElseThrow(() -> new RuntimeException("Film tidak ditemukan"));
        
        Long activeRentals = penyewaanRepository.countByFilmIdAndStatus(filmId, "DISEWA");
        return film.getStok() > activeRentals;
    }
    
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
    
    // Stats and Reports
    public long getTotalFilms() {
        return filmRepository.count();
    }
    
    public long getAvailableFilms() {
        return filmRepository.countByStokGreaterThan(0);
    }
}