package com.example.demo.controller;

import com.example.demo.dto.FilmDTO;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Film;
import com.example.demo.model.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.BookingService;
import com.example.demo.service.FilmService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    // Public Pages
    @GetMapping({ "/", "/home" })
    public String homepage(Model model, Authentication authentication) {
        try {
            if (authentication != null) {
                model.addAttribute("username", authentication.getName());
                model.addAttribute("role", authentication.getAuthorities().toString());
                return "homepage-authenticated";
            }

            // Add latest films for homepage
            model.addAttribute("latestFilms", filmService.getLatestFilms());
            model.addAttribute("popularFilms", filmService.getPopularFilms());
            return "homepage";
        } catch (Exception e) {
            logger.error("Error loading homepage", e);
            return "error/500";
        }
    }

    @GetMapping("/explore")
    public String showExplore(Model model,
            @RequestParam(required = false) String judul,
            @RequestParam(required = false) List<Long> genreIds,
            @RequestParam(required = false) List<Long> aktorIds,
            @RequestParam(required = false) Integer tahunRilis,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            // Get username
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("username", auth.getName());

            // Get films with filters
            Pageable pageable = PageRequest.of(page, size);
            Page<Film> filmPage = filmService.findFilmsWithFilters(judul, genreIds, aktorIds, tahunRilis, pageable);

            // Add to model
            model.addAttribute("films", filmPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", filmPage.getTotalPages());
            model.addAttribute("totalItems", filmPage.getTotalElements());

            // Add search parameters
            model.addAttribute("searchJudul", judul);
            model.addAttribute("searchGenreIds", genreIds);
            model.addAttribute("searchAktorIds", aktorIds);
            model.addAttribute("searchTahunRilis", tahunRilis);

            // Add filter options
            model.addAttribute("genres", filmService.getAllGenres());
            model.addAttribute("aktors", filmService.getAllActors());

            return "explore";
        } catch (Exception e) {
            logger.error("Error in showExplore", e);
            throw new RuntimeException("Failed to load explore page", e);
        }
    }

    @GetMapping("/login")
    public String loginPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/homepage-authenticated";
        }
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        try {
            Optional<Map<String, Object>> authResult = userService.authenticateUser(loginRequest);
            if (authResult.isPresent()) {
                // Cek role user
                Map<String, Object> userData = authResult.get();
                String role = (String) userData.get("role");

                // Redirect berdasarkan role
                if ("ADMIN".equals(role)) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/homepage-authenticated";
                }
            } else {
                model.addAttribute("error", "Username atau password salah");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Terjadi kesalahan saat login");
            return "login";
        }
    }

    // Registration
    @GetMapping("/register")
    public String registerPage(Model model, Authentication authentication) {
        if (authentication != null) {
            return "redirect:/";
        }
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Processing registration for username: {}", registerRequest.getUsername());

            // Validasi username dan email yang sudah ada
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Username sudah digunakan"));
            }
            if (userService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email sudah digunakan"));
            }

            // Validasi password match
            if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirm())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Password tidak cocok"));
            }

            // Register user
            User savedUser = userService.registerUser(registerRequest);

            logger.info("Successfully registered user with id: {}", savedUser.getId());
            return ResponseEntity.ok()
                    .body(Map.of("message", "Registrasi berhasil"));

        } catch (Exception e) {
            logger.error("Registration error: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Terjadi kesalahan saat registrasi: " + e.getMessage()));
        }
    }

    @PostMapping("/register-form")
    public String registerForm(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult result,
            Model model) {

        logger.info("Processing form registration for username: {}", registerRequest.getUsername());

        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Check if username exists
            if (userService.existsByUsername(registerRequest.getUsername())) {
                model.addAttribute("error", "Username sudah digunakan");
                return "register";
            }

            // Check if email exists
            if (userService.existsByEmail(registerRequest.getEmail())) {
                model.addAttribute("error", "Email sudah digunakan");
                return "register";
            }

            // Validate password match
            if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirm())) {
                model.addAttribute("error", "Password tidak cocok");
                return "register";
            }

            // Register user
            User savedUser = userService.registerUser(registerRequest);
            logger.info("Successfully registered user with id: {}", savedUser.getId());

            return "redirect:/login?registered=true";

        } catch (Exception e) {
            logger.error("Registration error: {}", e.getMessage());
            model.addAttribute("error", "Terjadi kesalahan saat registrasi");
            return "register";
        }
    }

    @GetMapping("/homepage-authenticated")
    public String homepageAuthenticated(Model model, Authentication authentication) {
        try {
            if (authentication != null) {
                // Add user data
                model.addAttribute("username", authentication.getName());
                model.addAttribute("role", authentication.getAuthorities().toString());

                // Add latest films
                model.addAttribute("latestFilms", filmService.getLatestFilms());

                // Gunakan getPopularFilms() sebagai rekomendasi
                model.addAttribute("recommendedFilms", filmService.getPopularFilms());

                // Add user rental history
                model.addAttribute("rentalHistory",
                        userService.getUserRentalHistory(authentication.getName()));

                return "homepage-authenticated";
            }

            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error loading authenticated homepage", e);
            return "error/500";
        }
    }

    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "redirect:/home?logout=success";
    }

    // User Profile
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            var userData = userService.getUserProfile(principal.getName());
            model.addAttribute("userData", userData);
            model.addAttribute("rentalHistory", userService.getUserRentalHistory(principal.getName()));
            return "profile";
        } catch (Exception e) {
            logger.error("Error loading profile", e);
            return "error/500";
        }
    }

    // API Endpoints
    @GetMapping("/user-data")
    @ResponseBody
    public ResponseEntity<?> getUserData(Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "User tidak terautentikasi"));
        }

        try {
            return ResponseEntity.ok(userService.getUserData(principal.getName()));
        } catch (Exception e) {
            logger.error("Error fetching user data", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Gagal mengambil data user"));
        }
    }

    @GetMapping("/history")
    public String rentalHistory(Model model, Authentication authentication) {
        try {
            if (authentication == null) {
                return "redirect:/login";
            }

            // Add username for navigation
            model.addAttribute("username", authentication.getName());

            // Get paginated rental history
            // Default to first page with 10 items per page
            Pageable pageable = PageRequest.of(0, 10);
            Page<Map<String, Object>> rentalHistory = userService.getPaginatedRentalHistory(
                    authentication.getName(),
                    pageable);

            // Add rental history data to model
            model.addAttribute("rentals", rentalHistory.getContent());
            model.addAttribute("currentPage", rentalHistory.getNumber());
            model.addAttribute("totalPages", rentalHistory.getTotalPages());
            model.addAttribute("totalItems", rentalHistory.getTotalElements());

            return "rental-history";
        } catch (Exception e) {
            logger.error("Error loading rental history", e);
            return "error/500";
        }
    }

    @GetMapping("/history/page")
    public String rentalHistoryPaginated(
            Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (authentication == null) {
                return "redirect:/login";
            }

            // Add username for navigation
            model.addAttribute("username", authentication.getName());

            // Get paginated rental history with requested page and size
            Pageable pageable = PageRequest.of(page, size);
            Page<Map<String, Object>> rentalHistory = userService.getPaginatedRentalHistory(
                    authentication.getName(),
                    pageable);

            // Add rental history data to model
            model.addAttribute("rentals", rentalHistory.getContent());
            model.addAttribute("currentPage", rentalHistory.getNumber());
            model.addAttribute("totalPages", rentalHistory.getTotalPages());
            model.addAttribute("totalItems", rentalHistory.getTotalElements());

            return "rental-history";
        } catch (Exception e) {
            logger.error("Error loading paginated rental history", e);
            return "error/500";
        }
    }

    // Helper Methods
    private String determineHomepage(Authentication authentication) {
        String role = authentication.getAuthorities().toString();
        if (role.contains("ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/home";
    }

    // @Autowired
    // private BookingService bookingService;

    // @GetMapping("/films/{id}/book")
    // public String showBookingPage(@PathVariable Long id, Model model) {
    //     try {
    //         // Menggunakan getFilmDto untuk mendapatkan detail film
    //         FilmDTO film = filmService.getFilmDto(id);

    //         // Get current logged-in username
    //         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //         String username = auth.getName();

    //         model.addAttribute("film", film);
    //         model.addAttribute("username", username);

    //         // Optional: Tambahkan informasi tambahan seperti sisa stok
    //         boolean alreadyBooked = bookingService.getUserActiveBookings(username).stream()
    //                 .anyMatch(booking -> booking.getFilm().getId().equals(id));
    //         model.addAttribute("alreadyBooked", alreadyBooked);

    //         return "bookfilm"; // Mengarahkan ke file bookfilm.html di templates
    //     } catch (Exception e) {
    //         logger.error("Error loading booking page for film id: " + id, e);
    //         return "error/404"; // Jika terjadi error, arahkan ke halaman 404
    //     }
    // }

    // @PostMapping("/films/{id}/book/confirm")
    // public String confirmBooking(@PathVariable Long id, @RequestParam int duration) {
    //     try {
    //         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //         String username = auth.getName();

    //         // Validasi film
    //         FilmDTO film = filmService.getFilmDto(id);
    //         if (film.getStok() <= 0) {
    //             return "redirect:/films/" + id + "/book?error=outofstock";
    //         }

    //         if (bookingService.isFilmAlreadyBooked(username, id)) {
    //             return "redirect:/films/" + id + "/book?error=alreadybooked";
    //         }
    //         System.out.println("POST request received for film ID: " + id + " with duration: " + duration);
    //         // Membuat booking
    //         bookingService.createBooking(id, username, duration);
    //         return "redirect:/films/" + id + "/book?success=true";
            
    //     } catch (Exception e) {
    //         logger.error("Error confirming booking for film id: " + id, e);
    //         return "redirect:/films/" + id + "/book?error=true";
    //     }
    // }

    // -------------------------------------------------------------------------------------------------
}