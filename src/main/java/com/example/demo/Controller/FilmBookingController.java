package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.FilmDTO;
import com.example.demo.model.User;
import com.example.demo.service.BookingService;
import com.example.demo.service.FilmService;
import com.example.demo.service.GenreService;
import com.example.demo.repository.UserRepository;

@Controller
@RequestMapping("/films")
public class FilmBookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(FilmBookingController.class);
    
    @Autowired
    private FilmService filmService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private GenreService genreService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{id}/book/confirm")
    public String confirmBooking(@PathVariable Long id, @RequestParam int duration) {
        logger.info("Menerima permintaan peminjaman - Film ID: {}, Durasi: {}", id, duration);
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Get user first
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
            
            logger.info("User {} mencoba meminjam film {}", username, id);

            FilmDTO film = filmService.getFilmDto(id);
            if (film == null) {
                logger.warn("Film dengan ID {} tidak ditemukan", id);
                return "redirect:/films/" + id + "/book?error=notfound";
            }
            
            logger.info("Film ditemukan: {}, Stok saat ini: {}", film.getJudul(), film.getStok());

            if (film.getStok() <= 0) {
                logger.warn("Film {} stok habis", film.getJudul());
                return "redirect:/films/" + id + "/book?error=outofstock";
            }

            if (bookingService.isFilmAlreadyBooked(user, id)) {
                logger.warn("Film {} sudah dipinjam oleh user {}", film.getJudul(), username);
                return "redirect:/films/" + id + "/book?error=alreadybooked";
            }

            bookingService.createBooking(id, username, duration);
            logger.info("Berhasil membuat peminjaman - Film: {}, User: {}", film.getJudul(), username);
            return "redirect:/history";

        } catch (Exception e) {
            logger.error("Error dalam proses peminjaman: {}", e.getMessage(), e);
            return "redirect:/films/" + id + "/book?error=true";
        }
    }

    @GetMapping("/{id}/book")
    public String showBookingForm(@PathVariable Long id, Model model) {
        try {
            FilmDTO film = filmService.getFilmDto(id);
            if (film == null) {
                logger.warn("Film dengan ID {} tidak ditemukan", id);
                return "error/404";
            }
            
            String genreName = genreService.getGenreNameById(film.getGenreId());
            film.setGenreNama(genreName);
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            model.addAttribute("film", film);
            model.addAttribute("username", username);
            
            return "bookfilm";
            
        } catch (Exception e) {
            logger.error("Error menampilkan form peminjaman: {}", e.getMessage(), e);
            return "error/404";
        }
    }
}