package com.example.demo.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.AktorDTO;
import com.example.demo.dto.FilmDTO;
import com.example.demo.dto.GenreDTO;
import com.example.demo.model.Penyewaan;
import com.example.demo.service.AdminService;
import com.example.demo.service.FilmService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    // Dashboard Page
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        try {

            // Get current logged in username
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            model.addAttribute("username", username);

            // Add dashboard data
            logger.debug("Getting total films...");
            model.addAttribute("totalFilms", filmService.getTotalFilms());
            logger.debug("Getting total active users...");
            model.addAttribute("totalActiveUsers", userService.getTotalActiveUsers());
            model.addAttribute("totalActiveRentals", adminService.getTotalActiveRentals());
            model.addAttribute("latestFilms", filmService.getLatestFilms());
            model.addAttribute("popularFilms", filmService.getPopularFilms());
            // model.addAttribute("recentRentals", adminService.getRecentRentals());

            return "admin/dashboard";
        } catch (Exception e) {
            logger.error("Error loading admin dashboard", e);
            throw new RuntimeException("Failed to load dashboard", e);
        }
    }

    // Films Management
    @GetMapping("/films")
    public String showFilmsPage(Model model) {
        try {
            model.addAttribute("films", filmService.getAllFilms());
            model.addAttribute("genres", filmService.getAllGenres());
            model.addAttribute("actors", filmService.getAllActors());
            return "admin/films";
        } catch (Exception e) {
            logger.error("Error loading films page", e);
            return "error/500";
        }
    }

    @PostMapping("/films/add")
    public String addFilm(@ModelAttribute FilmDTO filmDTO,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        try {
            filmService.addFilm(filmDTO, coverImage);
            return "redirect:/admin/films?success=true";
        } catch (Exception e) {
            logger.error("Error adding new film", e);
            return "redirect:/admin/films?error=true";
        }
    }

    @PostMapping("/films/update/{id}")
    public String updateFilm(@PathVariable Long id,
            @ModelAttribute FilmDTO filmDTO,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        try {
            filmService.updateFilm(id, filmDTO, coverImage);
            return "redirect:/admin/films?success=true";
        } catch (Exception e) {
            logger.error("Error updating film", e);
            return "redirect:/admin/films?error=true";
        }
    }

    @PostMapping("/films/delete/{id}")
    public String deleteFilm(@PathVariable Long id) {
        try {
            // Log the deletion attempt
            logger.info("Attempting to delete film with ID: {}", id);

            // Check if film exists
            if (!filmService.filmExists(id)) {
                logger.warn("Attempted to delete non-existent film with ID: {}", id);
                return "redirect:/admin/films?error=Film tidak ditemukan";
            }

            // Delete the film
            filmService.deleteFilm(id);

            logger.info("Successfully deleted film with ID: {}", id);
            return "redirect:/admin/films?success=Film berhasil dihapus";
        } catch (Exception e) {
            logger.error("Error deleting film with ID: " + id, e);
            return "redirect:/admin/films?error=Gagal menghapus film";
        }
    }

    @GetMapping("/films/get/{id}")
    @ResponseBody
    public ResponseEntity<?> getFilmById(@PathVariable Long id) {
        try {
            FilmDTO film = filmService.getFilmDto(id);
            return ResponseEntity.ok(film);
        } catch (Exception e) {
            logger.error("Error getting film with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Terjadi kesalahan saat mengambil data film");
        }
    }

    // Genre Management
    @GetMapping("/genres")
    public String showGenresPage(Model model) {
        try {
            model.addAttribute("genres", filmService.getAllGenres());
            return "admin/genres";
        } catch (Exception e) {
            logger.error("Error loading genres page", e);
            return "error/500";
        }
    }

    @PostMapping("/genres/add")
    public String addGenre(@ModelAttribute GenreDTO genreDTO) {
        try {
            filmService.addGenre(genreDTO);
            return "redirect:/admin/genres?success=true";
        } catch (Exception e) {
            logger.error("Error adding new genre", e);
            return "redirect:/admin/genres?error=true";
        }
    }

    @PostMapping("/genres/update/{id}")
    public String updateGenre(@PathVariable Long id, @ModelAttribute GenreDTO genreDTO) {
        try {
            filmService.updateGenre(id, genreDTO);
            return "redirect:/admin/genres?success=true";
        } catch (Exception e) {
            logger.error("Error updating genre", e);
            return "redirect:/admin/genres?error=true";
        }
    }

    @PostMapping("/genres/delete/{id}")
    public String deleteGenre(@PathVariable Long id) {
        try {
            filmService.deleteGenre(id);
            return "redirect:/admin/genres?success=true";
        } catch (Exception e) {
            logger.error("Error deleting genre", e);
            return "redirect:/admin/genres?error=true";
        }
    }

    // Actor Management
    @GetMapping("/actors")
    public String showActorsPage(Model model) {
        try {
            model.addAttribute("actors", filmService.getAllActors());
            return "admin/actors";
        } catch (Exception e) {
            logger.error("Error loading actors page", e);
            return "error/500";
        }
    }

    @PostMapping("/actors/add")
    public String addActor(@ModelAttribute AktorDTO aktorDTO,
            @RequestParam(value = "photoImage", required = false) MultipartFile photoImage) {
        try {
            filmService.addActor(aktorDTO, photoImage);
            return "redirect:/admin/actors?success=true";
        } catch (Exception e) {
            logger.error("Error adding new actor", e);
            return "redirect:/admin/actors?error=true";
        }
    }

    @PostMapping("/actors/update/{id}")
    public String updateActor(@PathVariable Long id,
            @ModelAttribute AktorDTO aktorDTO,
            @RequestParam(value = "photoImage", required = false) MultipartFile photoImage) {
        try {
            filmService.updateActor(id, aktorDTO, photoImage);
            return "redirect:/admin/actors?success=true";
        } catch (Exception e) {
            logger.error("Error updating actor", e);
            return "redirect:/admin/actors?error=true";
        }
    }

    @PostMapping("/actors/delete/{id}")
    public String deleteActor(@PathVariable Long id) {
        try {
            filmService.deleteActor(id);
            return "redirect:/admin/actors?success=true";
        } catch (Exception e) {
            logger.error("Error deleting actor", e);
            return "redirect:/admin/actors?error=true";
        }
    }

    // Users Management

    // Rentals Management
    @GetMapping("/rentals")
    public String showRentalsPage(Model model) {
        model.addAttribute("activeRentals", adminService.getActiveRentals());
        model.addAttribute("returnedRentals", adminService.getReturnedRentals());
        return "admin/rentals";
    }

    @PostMapping("/rentals/{id}/return")
    @ResponseBody
    public ResponseEntity<String> returnRental(@PathVariable("id") Long id) {
        try {
            adminService.returnRental(id);
            return ResponseEntity.ok("Rental successfully returned");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing return: " + e.getMessage());
        }
    }

    @GetMapping("/rentals/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRentalDetails(@PathVariable("id") Long id) {
        try {
            Penyewaan rental = adminService.getRentalDetails(id);
            Map<String, Object> response = new HashMap<>();
            
            response.put("rental", rental);
            response.put("basePrice", rental.getBasicPrice());
            response.put("lateFee", rental.getCurrentLateFee());
            response.put("totalPrice", rental.getTotalPrice());
            response.put("daysOverdue", rental.getCurrentDaysOverdue());
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting rental details: ", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Reports and Analytics
    @GetMapping("/reports")
    public String showReportsPage(Model model) {
        try {
            model.addAttribute("monthlyStats", adminService.getMonthlyStats());
            model.addAttribute("yearlyStats", adminService.getYearlyStats());
            return "admin/reports";
        } catch (Exception e) {
            logger.error("Error loading reports page", e);
            return "error/500";
        }
    }

    @GetMapping("/reports/rental-statistics")
    public ResponseEntity<?> getRentalStatistics() {
        try {
            return ResponseEntity.ok(adminService.getRentalStatistics());
        } catch (Exception e) {
            logger.error("Error fetching rental statistics", e);
            return ResponseEntity.badRequest().body("Error fetching statistics");
        }
    }

    @GetMapping("/reports/monthly")
    public ResponseEntity<?> getMonthlyReport(@RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        try {
            return ResponseEntity.ok(adminService.getMonthlyReport(year, month));
        } catch (Exception e) {
            logger.error("Error generating monthly report", e);
            return ResponseEntity.badRequest().body("Error generating report");
        }
    }

    @GetMapping("/reports/download/monthly")
    public ResponseEntity<?> downloadMonthlyReport(@RequestParam int year,
            @RequestParam int month) {
        try {
            return adminService.generateMonthlyReportPDF(year, month);
        } catch (Exception e) {
            logger.error("Error downloading monthly report", e);
            return ResponseEntity.badRequest().body("Error downloading report");
        }
    }

    @GetMapping("/api/admin/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // Menggunakan method yang sudah ada di AdminService
            statistics.put("totalRentals", adminService.getTotalActiveRentals());

            // Mengambil data dari getMonthlyStats() yang sudah ada
            Map<String, Object> monthlyStats = adminService.getMonthlyStats();
            statistics.put("monthlyRentals", monthlyStats.get("totalRentals"));
            statistics.put("popularFilms", monthlyStats.get("popularFilms"));
            statistics.put("genreDistribution", monthlyStats.get("rentalsByGenre"));
            statistics.put("customerStats", monthlyStats.get("customerStats"));

            // Mengambil data dari getSystemMetrics() yang sudah ada
            Map<String, Object> metrics = adminService.getSystemMetrics();
            statistics.put("totalRentals", metrics.get("totalRentals"));
            statistics.put("activeRentals", metrics.get("activeRentals"));
            statistics.put("overdueRentals", metrics.get("overdueRentals"));

            // Stock alerts dari AdminService
            Map<String, Object> stockAlerts = adminService.getStockAlerts();
            statistics.put("stockAlerts", stockAlerts.get("lowStock"));
            statistics.put("outOfStock", stockAlerts.get("outOfStock"));

            // Rental trends
            Map<String, Object> trends = adminService.getRentalTrends();
            statistics.put("dailyRentals", trends.get("dailyData"));

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting statistics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
