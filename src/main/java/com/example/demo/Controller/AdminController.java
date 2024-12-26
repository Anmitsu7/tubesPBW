package com.example.demo.controller;

import com.example.demo.dto.FilmDTO;
import com.example.demo.dto.GenreDTO;
import com.example.demo.dto.AktorDTO;
import com.example.demo.service.AdminService;
import com.example.demo.service.FilmService;
import com.example.demo.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

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
            // Add dashboard data
            model.addAttribute("totalFilms", filmService.getTotalFilms());
            model.addAttribute("totalActiveUsers", userService.getTotalActiveUsers());
            model.addAttribute("totalActiveRentals", adminService.getTotalActiveRentals());
            model.addAttribute("latestFilms", filmService.getLatestFilms());
            model.addAttribute("popularFilms", filmService.getPopularFilms());
            model.addAttribute("recentRentals", adminService.getRecentRentals());
            
            return "admin/dashboard";
        } catch (Exception e) {
            logger.error("Error loading admin dashboard", e);
            return "error/500";
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
                         @RequestParam("coverImage") MultipartFile coverImage) {
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
            filmService.deleteFilm(id);
            return "redirect:/admin/films?success=true";
        } catch (Exception e) {
            logger.error("Error deleting film", e);
            return "redirect:/admin/films?error=true";
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
                          @RequestParam("photoImage") MultipartFile photoImage) {
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
    @GetMapping("/users")
    public String showUsersPage(Model model) {
        try {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("totalUsers", userService.getTotalUsers());
            return "admin/users";
        } catch (Exception e) {
            logger.error("Error loading users page", e);
            return "error/500";
        }
    }

    // Rentals Management
    @GetMapping("/rentals")
    public String showRentalsPage(Model model) {
        try {
            model.addAttribute("activeRentals", adminService.getActiveRentals());
            model.addAttribute("returnedRentals", adminService.getReturnedRentals());
            return "admin/rentals";
        } catch (Exception e) {
            logger.error("Error loading rentals page", e);
            return "error/500";
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

    // Settings Management
    @GetMapping("/settings")
    public String showSettingsPage(Model model) {
        try {
            model.addAttribute("settings", adminService.getAdminSettings());
            return "admin/settings";
        } catch (Exception e) {
            logger.error("Error loading settings page", e);
            return "error/500";
        }
    }
}