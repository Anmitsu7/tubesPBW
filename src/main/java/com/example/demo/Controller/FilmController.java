package com.example.demo.controller;

import com.example.demo.service.FilmService;
import com.example.demo.service.GenreService;
import com.example.demo.dto.FilmDTO;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FilmController {
    private final FilmService filmService;
    private final GenreService genreService;

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
}