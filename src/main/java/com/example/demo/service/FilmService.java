package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.AktorDTO;
import com.example.demo.dto.FilmDTO;
import com.example.demo.dto.GenreDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.FilmMapper;
import com.example.demo.model.Aktor;
import com.example.demo.model.Film;
import com.example.demo.model.Genre;
import com.example.demo.repository.AktorRepository;
import com.example.demo.repository.FilmRepository;
import com.example.demo.repository.GenreRepository;
import com.example.demo.repository.PenyewaanRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

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
    private AktorRepository aktorRepository;

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

    @Transactional
    public FilmDTO addFilm(FilmDTO filmDTO, MultipartFile coverFile) {
        try {
            // Handle file upload
            if (coverFile != null && !coverFile.isEmpty()) {
                String originalFilename = StringUtils.cleanPath(coverFile.getOriginalFilename());
                String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

                // Path ke folder covers yang sudah ada
                Path targetLocation = Paths.get("src/main/resources/static/covers")
                        .toAbsolutePath()
                        .normalize();

                // Pastikan directory ada
                Files.createDirectories(targetLocation);

                // Copy file ke lokasi target
                Path targetPath = targetLocation.resolve(fileName);
                Files.copy(coverFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Set URL relatif untuk akses web
                filmDTO.setCoverUrl(fileName);
            }

            Film film = filmMapper.toEntity(filmDTO);

            // Set genre
            Genre genre = genreRepository.findById(filmDTO.getGenreId())
                    .orElseThrow(() -> new RuntimeException("Genre tidak ditemukan"));
            film.setGenre(genre);

            // Set actors
            Set<Aktor> actors = new HashSet<>();
            for (Long aktorId : filmDTO.getAktorIds()) {
                Aktor aktor = aktorRepository.findById(aktorId)
                        .orElseThrow(() -> new RuntimeException("Aktor tidak ditemukan: " + aktorId));
                actors.add(aktor);
            }
            film.setActors(actors);

            Film savedFilm = filmRepository.save(film);
            return filmMapper.toDto(savedFilm);
        } catch (Exception e) {
            logger.error("Error adding film: {}", e.getMessage());
            throw new RuntimeException("Gagal menambah film", e);
        }
    }

    @Transactional
    public void updateFilm(Long id, FilmDTO filmDTO, MultipartFile coverImage) {
        try {
            Film film = filmRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Film tidak ditemukan"));

            film.setJudul(filmDTO.getJudul());
            film.setDeskripsi(filmDTO.getDeskripsi());
            film.setTahunRilis(filmDTO.getTahunRilis());
            film.setStok(filmDTO.getStok());

            // Handle genre
            if (filmDTO.getGenreId() != null) {
                Genre genre = genreRepository.findById(filmDTO.getGenreId())
                        .orElseThrow(() -> new RuntimeException("Genre tidak ditemukan"));
                film.setGenre(genre);
            }

            // Handle actors
            if (filmDTO.getAktorIds() != null) {
                Set<Aktor> actors = new HashSet<>(aktorRepository.findAllById(filmDTO.getAktorIds()));
                film.setActors(actors);
            }

            // Handle cover image
            if (coverImage != null && !coverImage.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + coverImage.getOriginalFilename();
                Path filePath = Paths.get("src/main/resources/static/covers").resolve(fileName);
                Files.copy(coverImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                film.setCoverUrl(fileName);
            }
            // Jika tidak ada cover baru diupload, pertahankan cover lama
            // TIDAK PERLU mengubah coverUrl film

            filmRepository.save(film);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengupdate film", e);
        }
    }

    @Transactional
    public Film updateFilmWithCover(Long id, FilmDTO filmDTO, MultipartFile coverFile) {
        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film tidak ditemukan"));

        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                // Delete old cover file if exists
                if (existingFilm.getCoverUrl() != null) {
                    Path oldCoverPath = Paths.get(uploadDirectory,
                            existingFilm.getCoverUrl().substring("/covers/".length()));
                    Files.deleteIfExists(oldCoverPath);
                }

                // Save new cover
                String fileName = UUID.randomUUID().toString() + "_"
                        + StringUtils.cleanPath(coverFile.getOriginalFilename());
                Path filePath = Paths.get(uploadDirectory).resolve(fileName);
                Files.copy(coverFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                filmDTO.setCoverUrl("/covers/" + fileName);
            } catch (Exception e) {
                throw new RuntimeException("Gagal mengupdate cover film", e);
            }
        }

        Film updatedFilm = filmMapper.toEntity(filmDTO);
        updatedFilm.setId(existingFilm.getId());
        return filmRepository.save(updatedFilm);
    }

    @Transactional
    public void updateStok(Long filmId, Integer newStok) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Film tidak ditemukan"));
        film.setStok(newStok);
        filmRepository.save(film);
    }

    public List<FilmDTO> getLowStockFilms(int threshold) {
        return filmRepository.findByStokLessThan(threshold).stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
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
            Film film = filmRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Film tidak ditemukan"));

            // Optional: Log penyewaan yang akan dihapus
            Long activeRentals = penyewaanRepository.countByFilmIdAndStatus(id, "DISEWA");
            if (activeRentals > 0) {
                logger.warn("Menghapus film {} yang memiliki {} penyewaan aktif", id, activeRentals);
            }

            // Delete film - this will cascade to related rentals
            filmRepository.delete(film);

            logger.info("Film berhasil dihapus: {}", id);
        } catch (Exception e) {
            logger.error("Error menghapus film: {}", e.getMessage());
            throw new RuntimeException("Gagal menghapus film: " + e.getMessage(), e);
        }
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

    public Page<FilmDTO> searchFilmsAdvanced(String judul, Long genreId, Integer tahunRilis,
            Boolean available, Pageable pageable) {
        Page<Film> films = filmRepository.searchFilms(judul, genreId, tahunRilis, available, pageable);
        return films.map(filmMapper::toDto);
    }

    public boolean isFilmAvailable(Long filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film tidak ditemukan"));

        Long activeRentals = penyewaanRepository.countByFilmIdAndStatus(filmId, "DISEWA");
        return film.getStok() > activeRentals;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public long getTotalFilms() {
        return filmRepository.count();
    }

    public long getAvailableFilms() {
        return filmRepository.countByStokGreaterThan(0);
    }

    public Map<String, Object> getFilmStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFilms", filmRepository.count());
        stats.put("availableFilms", filmRepository.countByStokGreaterThan(0));
        stats.put("filmsByGenre", filmRepository.getFilmCountByGenre());
        stats.put("filmsByYear", filmRepository.getFilmCountByYear());
        return stats;
    }

    public List<Map<String, Object>> getFilmRentalStatistics(LocalDate startDate, LocalDate endDate) {
        List<Object[]> stats = filmRepository.getFilmRentalStats(startDate, endDate);
        return stats.stream()
                .map(arr -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("film", arr[0]);
                    map.put("rentalCount", arr[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    private void validateFilmDTO(FilmDTO filmDTO) {
        if (filmDTO.getStok() < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        if (filmDTO.getTahunRilis() > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Tahun rilis tidak valid");
        }
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File harus berupa gambar");
        }
    }

    @Transactional
    public List<Film> createFilmBatch(List<FilmDTO> filmDTOs) {
        return filmDTOs.stream()
                .map(dto -> createFilm(dto, null))
                .collect(Collectors.toList());
    }

    @Transactional
    public GenreDTO addGenre(GenreDTO genreDTO) {
        try {
            Genre genre = new Genre();
            genre.setNama(genreDTO.getNama());
            Genre savedGenre = genreRepository.save(genre);
            return new GenreDTO(savedGenre.getId(), savedGenre.getNama());
        } catch (Exception e) {
            logger.error("Error adding genre: {}", e.getMessage());
            throw new RuntimeException("Gagal menambah genre", e);
        }
    }

    @Transactional
    public GenreDTO updateGenre(Long id, GenreDTO genreDTO) {
        try {
            Genre genre = genreRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Genre tidak ditemukan"));
            genre.setNama(genreDTO.getNama());
            Genre updatedGenre = genreRepository.save(genre);
            return new GenreDTO(updatedGenre.getId(), updatedGenre.getNama());
        } catch (Exception e) {
            logger.error("Error updating genre: {}", e.getMessage());
            throw new RuntimeException("Gagal mengupdate genre", e);
        }
    }

    @Transactional
    public void deleteGenre(Long id) {
        try {
            if (!genreRepository.existsById(id)) {
                throw new RuntimeException("Genre tidak ditemukan");
            }
            genreRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting genre: {}", e.getMessage());
            throw new RuntimeException("Gagal menghapus genre", e);
        }
    }

    public List<AktorDTO> getAllActors() {
        return aktorRepository.findAll().stream()
                .map(aktor -> {
                    AktorDTO dto = new AktorDTO();
                    dto.setId(aktor.getId());
                    dto.setNama(aktor.getNama());
                    dto.setNegaraAsal(aktor.getNegaraAsal());
                    dto.setFotoUrl(aktor.getFotoUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public AktorDTO addActor(AktorDTO aktorDTO, MultipartFile photo) {
        try {
            // Handle photo upload
            if (photo != null && !photo.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
                Path filePath = Paths.get(uploadDirectory, "actors", fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                aktorDTO.setFotoUrl("/actors/" + fileName);
            }

            Aktor aktor = new Aktor();
            aktor.setNama(aktorDTO.getNama());
            aktor.setNegaraAsal(aktorDTO.getNegaraAsal());
            aktor.setFotoUrl(aktorDTO.getFotoUrl());

            Aktor savedAktor = aktorRepository.save(aktor);
            return new AktorDTO(savedAktor.getId(), savedAktor.getNama(),
                    savedAktor.getNegaraAsal(), savedAktor.getFotoUrl());
        } catch (Exception e) {
            logger.error("Error adding actor: {}", e.getMessage());
            throw new RuntimeException("Gagal menambah aktor", e);
        }
    }

    @Transactional
    public AktorDTO updateActor(Long id, AktorDTO aktorDTO, MultipartFile photo) {
        try {
            Aktor aktor = aktorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Aktor tidak ditemukan"));

            // Handle photo upload
            if (photo != null && !photo.isEmpty()) {
                // Delete old photo if exists
                if (aktor.getFotoUrl() != null) {
                    Path oldPath = Paths.get(uploadDirectory, aktor.getFotoUrl());
                    Files.deleteIfExists(oldPath);
                }

                String fileName = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
                Path filePath = Paths.get(uploadDirectory, "actors", fileName);
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                aktor.setFotoUrl("/actors/" + fileName);
            }

            aktor.setNama(aktorDTO.getNama());
            aktor.setNegaraAsal(aktorDTO.getNegaraAsal());

            Aktor updatedAktor = aktorRepository.save(aktor);
            return new AktorDTO(updatedAktor.getId(), updatedAktor.getNama(),
                    updatedAktor.getNegaraAsal(), updatedAktor.getFotoUrl());
        } catch (Exception e) {
            logger.error("Error updating actor: {}", e.getMessage());
            throw new RuntimeException("Gagal mengupdate aktor", e);
        }
    }

    @Transactional
    public void deleteActor(Long id) {
        try {
            Aktor aktor = aktorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Aktor tidak ditemukan"));

            // Delete photo if exists
            if (aktor.getFotoUrl() != null) {
                Path photoPath = Paths.get(uploadDirectory, aktor.getFotoUrl());
                Files.deleteIfExists(photoPath);
            }

            aktorRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting actor: {}", e.getMessage());
            throw new RuntimeException("Gagal menghapus aktor", e);
        }
    }

    public Page<Film> findFilmsWithFilters(String judul,
            List<Long> genreIds,
            List<Long> aktorIds,
            Integer tahunRilis,
            Pageable pageable) {

        Specification<Film> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (judul != null && !judul.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("judul")),
                        "%" + judul.toLowerCase() + "%"));
            }

            if (genreIds != null && !genreIds.isEmpty()) {
                predicates.add(root.get("genre").get("id").in(genreIds));
            }

            if (aktorIds != null && !aktorIds.isEmpty()) {
                Join<Film, Aktor> aktorJoin = root.join("actors");
                predicates.add(aktorJoin.get("id").in(aktorIds));
            }

            if (tahunRilis != null) {
                predicates.add(cb.equal(root.get("tahunRilis"), tahunRilis));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return filmRepository.findAll(spec, pageable);
    }

    public boolean filmExists(Long id) {
        // Implementation depends on your repository
        return filmRepository.existsById(id);
    }

    public List<FilmDTO> getAllFilms() {
        return filmRepository.findAllWithGenre().stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }
}