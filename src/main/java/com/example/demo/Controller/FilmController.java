package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.FilmDTO;
import com.example.demo.service.FilmService;
import com.example.demo.service.GenreService;

@Controller
public class FilmController {
    private final FilmService filmService;
    private final GenreService genreService;
 @Autowired
    private JdbcTemplate jdbcTemplate;
    public FilmController(FilmService filmService, GenreService genreService) {
        this.filmService = filmService;
        this.genreService = genreService;
    }

    @GetMapping("/explore/search")
    public String searchFilms(
            @RequestParam(required = false) String judul,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer tahunRilis,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model,
            Authentication authentication
    ) {
        try {
            if (authentication != null) {
                model.addAttribute("username", authentication.getName());
                model.addAttribute("role", authentication.getAuthorities().toString());
            }

            // Menggunakan PageRequest untuk paginasi
            PageRequest pageable = PageRequest.of(page, size);
            Page<FilmDTO> searchResults = filmService.searchFilmsAdvanced(
                judul, 
                genreId, 
                tahunRilis, 
                null, // tidak menggunakan available filter
                pageable
            );
            
            // Menambahkan hasil pencarian ke model
            model.addAttribute("films", searchResults.getContent());
            model.addAttribute("genres", genreService.getAllGenres());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", searchResults.getTotalPages());
            
            // Mempertahankan parameter pencarian
            model.addAttribute("searchJudul", judul);
            model.addAttribute("searchGenreId", genreId);
            model.addAttribute("searchTahunRilis", tahunRilis);
            
            return "explore";
        } catch (Exception e) {
            return "error/500";
        }
    }
  @DeleteMapping("/films/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteFilm(@PathVariable Long id) {
        try {
            filmService.deleteFilm(id);
            return ResponseEntity.ok()
                .body("{\"message\": \"Film berhasil dihapus\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("{\"error\": \"Gagal menghapus film: " + e.getMessage() + "\"}");
        }
    }
}

