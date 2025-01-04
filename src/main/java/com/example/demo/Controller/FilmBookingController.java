package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.FilmDTO;
import com.example.demo.service.BookingService;
import com.example.demo.service.FilmService;
import com.example.demo.service.GenreService;
@Controller
@RequestMapping("/films")
public class FilmBookingController {
    private final FilmService filmService;
    private final BookingService bookingService;
    private final GenreService genreService;

    @Autowired
    public FilmBookingController(BookingService bookingService, FilmService filmService, GenreService genreService) {
        this.bookingService = bookingService;
        this.filmService = filmService;
        this.genreService = genreService;
    }

    @GetMapping("/{id}/book")
    public String showBookingForm(@PathVariable Long id, Model model) {
        try {
            FilmDTO film = filmService.getFilmDto(id);
            
            // Set genre name
            String genreName = genreService.getGenreNameById(film.getGenreId());
            film.setGenreNama(genreName);
            
            // Add username to model for navigation
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            model.addAttribute("film", film);
            model.addAttribute("username", username);
            
            return "bookfilm";
        } catch (Exception e) {
            return "error/404";
        }
    }

    @PostMapping("/{id}/book/confirm")
    public String confirmBooking(@PathVariable Long id, @RequestParam int duration) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            FilmDTO film = filmService.getFilmDto(id);
            if (film.getStok() <= 0) {
                return "redirect:/films/" + id + "/book?error=outofstock";
            }

            if (bookingService.isFilmAlreadyBooked(username, id)) {
                return "redirect:/films/" + id + "/book?error=alreadybooked";
            }

            bookingService.createBooking(id, username, duration);
            return "redirect:/films/" + id + "/book?success=true";
        } catch (Exception e) {
            return "redirect:/films/" + id + "/book?error=true";
        }
    }
}