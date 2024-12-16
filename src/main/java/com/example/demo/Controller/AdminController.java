package com.example.demo.controller;

import com.example.demo.dto.FilmDTO;
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
    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model) {
        try {
            // Add dashboard data
            model.addAttribute("totalFilms", filmService.getTotalFilms());
            model.addAttribute("totalActiveUsers", userService.getTotalActiveUsers());
            model.addAttribute("totalActiveRentals", adminService.getTotalActiveRentals());
            model.addAttribute("latestFilms", filmService.getLatestFilms());
            model.addAttribute("popularFilms", filmService.getPopularFilms());
            model.addAttribute("recentRentals", adminService.getRecentRentals());
            
            return "admin/Homepage";
        } catch (Exception e) {
            logger.error("Error loading admin dashboard", e);
            return "error/500";
        }
    }

    // Films Management Page
    @GetMapping("/films")
    public String showFilmsPage(Model model) {
        try {
            model.addAttribute("films", filmService.getAllFilms());
            model.addAttribute("genres", filmService.getAllGenres());
            return "admin/films";
        } catch (Exception e) {
            logger.error("Error loading films page", e);
            return "error/500";
        }
    }

    // Users Management Page
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

    // Rentals Management Page
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

    // Reports Page
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

    // Settings Page
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