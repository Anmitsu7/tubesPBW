package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired 
    private FilmRepository filmRepository;
    
    @Autowired 
    private PenyewaanRepository penyewaanRepository;
    
    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AktorRepository aktorRepository;

    @Value("${file.upload.directory}")
    private String uploadDirectory;

    // Dashboard Methods
    public long getTotalActiveRentals() {
        return penyewaanRepository.countByStatus("DISEWA");
    }

    public List<Penyewaan> getRecentRentals() {
        return penyewaanRepository.findTop10ByOrderByTanggalSewaDesc();
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalFilms", filmRepository.count());
            stats.put("activeRentals", getTotalActiveRentals());
            stats.put("totalUsers", userRepository.count());
            stats.put("recentRentals", getRecentRentals());
            stats.put("popularFilms", getPopularFilms());
            stats.put("lowStockFilms", filmRepository.findLowStockFilms(5));
            stats.put("overdueRentals", penyewaanRepository.findAllOverdueRentals());
        } catch (Exception e) {
            logger.error("Error getting dashboard stats: {}", e.getMessage());
        }
        return stats;
    }

    public Map<String, Object> getEnhancedDashboardStats() {
        Map<String, Object> enhancedStats = getDashboardStats();
        enhancedStats.put("stockAlerts", getStockAlerts());
        enhancedStats.put("rentalTrends", getRentalTrends());
        enhancedStats.put("customerInsights", getCustomerInsights());
        return enhancedStats;
    }

    // Genre Management
    @Transactional
    public Genre addGenre(Genre genre) {
        try {
            logger.info("Adding new genre: {}", genre.getNama());
            return genreRepository.save(genre);
        } catch (Exception e) {
            logger.error("Error adding genre: {}", e.getMessage());
            throw new RuntimeException("Gagal menambahkan genre", e);
        }
    }

    @Transactional
    public void updateGenre(Long id, Genre genre) {
        try {
            Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre tidak ditemukan"));
            existingGenre.setNama(genre.getNama());
            genreRepository.save(existingGenre);
            logger.info("Genre updated successfully: {}", id);
        } catch (Exception e) {
            logger.error("Error updating genre: {}", e.getMessage());
            throw new RuntimeException("Gagal mengupdate genre", e);
        }
    }

    @Transactional
    public void deleteGenre(Long id) {
        try {
            genreRepository.deleteById(id);
            logger.info("Genre deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting genre: {}", e.getMessage());
            throw new RuntimeException("Gagal menghapus genre", e);
        }
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    // Actor Management

    private void validateImageFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("File harus berupa gambar");
            }
            
            long fileSize = file.getSize();
            if (fileSize > 5 * 1024 * 1024) { // 5MB limit
                throw new IllegalArgumentException("Ukuran file terlalu besar (max 5MB)");
            }
        }
    }

    @Transactional
    public Aktor addAktor(Aktor aktor, MultipartFile foto) {
        try {
            validateImageFile(foto);
            if (foto != null && !foto.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
                Path filePath = Paths.get(uploadDirectory, "actors", fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                aktor.setFotoUrl("/actors/" + fileName);
            }
            logger.info("Adding new actor: {}", aktor.getNama());
            return aktorRepository.save(aktor);
        } catch (Exception e) {
            logger.error("Error adding actor: {}", e.getMessage());
            throw new RuntimeException("Gagal menambahkan aktor", e);
        }
    }

    @Transactional
    public void updateAktor(Long id, Aktor aktor, MultipartFile foto) {
        try {
            Aktor existingAktor = aktorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aktor tidak ditemukan"));

            if (foto != null && !foto.isEmpty()) {
                // Delete old photo if exists
                if (existingAktor.getFotoUrl() != null) {
                    Path oldPath = Paths.get(uploadDirectory, existingAktor.getFotoUrl());
                    Files.deleteIfExists(oldPath);
                }

                // Save new photo
                String fileName = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
                Path filePath = Paths.get(uploadDirectory, "actors", fileName);
                Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                existingAktor.setFotoUrl("/actors/" + fileName);
            }

            existingAktor.setNama(aktor.getNama());
            existingAktor.setNegaraAsal(aktor.getNegaraAsal());
            aktorRepository.save(existingAktor);
            logger.info("Actor updated successfully: {}", id);
        } catch (Exception e) {
            logger.error("Error updating actor: {}", e.getMessage());
            throw new RuntimeException("Gagal mengupdate aktor", e);
        }
    }

    @Transactional
    public void deleteAktor(Long id) {
        try {
            Aktor aktor = aktorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aktor tidak ditemukan"));
            
            // Delete photo file if exists
            if (aktor.getFotoUrl() != null) {
                Path photoPath = Paths.get(uploadDirectory, aktor.getFotoUrl());
                Files.deleteIfExists(photoPath);
            }
            
            aktorRepository.deleteById(id);
            logger.info("Actor deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting actor: {}", e.getMessage());
            throw new RuntimeException("Gagal menghapus aktor", e);
        }
    }

    public List<Aktor> getAllAktors() {
        return aktorRepository.findAll();
    }

    // Rental Management
    public List<Penyewaan> getActiveRentals() {
        return penyewaanRepository.findByStatus("DISEWA");
    }

    public List<Penyewaan> getReturnedRentals() {
        return penyewaanRepository.findByStatus("DIKEMBALIKAN");
    }

    // Stock Management
    @Transactional
    public void updateFilmStock(Long filmId, int adjustment) {
        Film film = filmRepository.findById(filmId)
            .orElseThrow(() -> new RuntimeException("Film tidak ditemukan"));
        film.setStok(film.getStok() + adjustment);
        filmRepository.save(film);
        logger.info("Stock updated for film {}: {}", filmId, adjustment);
    }

    // Statistics & Reports
    public Map<String, Object> getMonthlyStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalRentals", penyewaanRepository.count());
            stats.put("activeRentals", getTotalActiveRentals());
            stats.put("popularFilms", penyewaanRepository.findMostRentedFilms(PageRequest.of(0, 10)));
            stats.put("rentalsByGenre", penyewaanRepository.getGenrePopularity(LocalDate.now().getYear()));
            stats.put("customerStats", getCustomerInsights());
        } catch (Exception e) {
            logger.error("Error getting monthly stats: {}", e.getMessage());
        }
        return stats;
    }

    public Map<String, Object> getYearlyStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("yearlyRentals", penyewaanRepository.countRentalsByYear());
            stats.put("yearlyRevenue", calculateYearlyRevenue());
            stats.put("popularGenres", penyewaanRepository.getGenrePopularity(LocalDate.now().getYear()));
            stats.put("topCustomers", penyewaanRepository.getTopCustomers(10));
        } catch (Exception e) {
            logger.error("Error getting yearly stats: {}", e.getMessage());
        }
        return stats;
    }

    public Map<String, Object> getRentalStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        try {
            statistics.put("dailyRentals", penyewaanRepository.getDailyRentals(
                startOfMonth, today));
            statistics.put("monthlyRentals", penyewaanRepository.getMonthlyRentals(
                today.getYear()));
            statistics.put("popularMovies", penyewaanRepository.getPopularMovies(
                today.getYear(), today.getMonthValue(), PageRequest.of(0, 10)));
            statistics.put("genrePopularity", penyewaanRepository.getGenrePopularity(
                today.getYear()));
            statistics.put("customerAnalytics", penyewaanRepository.getTopCustomers(10));
        } catch (Exception e) {
            logger.error("Error getting rental statistics: {}", e.getMessage());
        }
        return statistics;
    }

    public Map<String, Object> generateMonthlyReport(int year, int month) {
        Map<String, Object> report = new HashMap<>();
        try {
            report.put("rentalSummary", penyewaanRepository.getRentalSummary(year, month));
            report.put("popularMovies", penyewaanRepository.getPopularMovies(
                year, month, PageRequest.of(0, 5)));
            report.put("customerStats", penyewaanRepository.getTopCustomers(5));
            report.put("returnRateAnalysis", penyewaanRepository.getReturnRateAnalysis(
                LocalDate.of(year, month, 1),
                LocalDate.of(year, month, 1).plusMonths(1).minusDays(1)));
        } catch (Exception e) {
            logger.error("Error generating monthly report: {}", e.getMessage());
        }
        return report;
    }

    // User Statistics
    public List<Map<String, Object>> getUserStatistics() {
        return userRepository.findActiveUsers().stream()
            .map(user -> {
                Map<String, Object> userStats = new HashMap<>();
                userStats.put("userId", user.getId());
                userStats.put("username", user.getUsername());
                userStats.put("totalRentals", penyewaanRepository.countByPenggunaId(user.getId()));
                userStats.put("activeRentals", penyewaanRepository.countByPenggunaIdAndStatus(
                    user.getId(), "DISEWA"));
                return userStats;
            })
            .collect(Collectors.toList());
    }

    // System Status
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("outOfStockFilms", filmRepository.countOutOfStockFilms());
        status.put("overdueRentals", penyewaanRepository.findAllOverdueRentals().size());
        status.put("activeUsers", userRepository.countActiveUsers());
        return status;
    }

    // Settings
    public Map<String, Object> getAdminSettings() {
        Map<String, Object> settings = new HashMap<>();
        try {
            settings.put("systemSettings", getSystemSettings());
            settings.put("userSettings", getUserSettings());
        } catch (Exception e) {
            logger.error("Error getting admin settings: {}", e.getMessage());
        }
        return settings;
    }

    // Private Helper Methods
    private List<Film> getPopularFilms() {
        return filmRepository.findMostRentedFilms(PageRequest.of(0, 5));
    }

    private Map<String, Object> getStockAlerts() {
        Map<String, Object> alerts = new HashMap<>();
        alerts.put("lowStock", filmRepository.findLowStockFilms(5));
        alerts.put("outOfStock", filmRepository.findOutOfStockFilms());
        return alerts;
    }

    private Map<String, Object> getRentalTrends() {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> dailyRentals = 
            penyewaanRepository.getDailyRentals(today.minusDays(30), today);
        
        Map<String, Object> trends = new HashMap<>();
        trends.put("dailyData", dailyRentals);
        trends.put("totalRentals", dailyRentals.stream()
            .mapToInt(m -> ((Number)m.get("total_rentals")).intValue())
            .sum());
        return trends;
    }

    private Map<String, Object> getCustomerInsights() {
        List<Map<String, Object>> topCustomers = penyewaanRepository.getTopCustomers(5);
        Map<String, Object> insights = new HashMap<>();
        insights.put("topCustomers", topCustomers);
        insights.put("totalCustomers", topCustomers.size());
        return insights;
    }

    private Double calculateYearlyRevenue() {
        int currentYear = LocalDate.now().getYear();
        List<Penyewaan> yearlyRentals = penyewaanRepository
            .findByTanggalSewaBetween(
                LocalDate.of(currentYear, 1, 1),
                LocalDate.of(currentYear, 12, 31)
            );
            
        return yearlyRentals.stream()
            .mapToDouble(rental -> {
                double basePrice = 5000.0; // Harga dasar sewa
                double lateFee = rental.isOverdue() ? 
                    rental.getDurationInDays() * 1000.0 : 0.0; // Denda keterlambatan
                return basePrice + lateFee;
            })
            .sum();
    }

    private Map<String, Object> getSystemSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("rentalDuration", 7); // Durasi sewa default (hari)
        settings.put("lateFeePerDay", 1000.0); // Denda per hari
        settings.put("baseRentalPrice", 5000.0); // Harga sewa dasar
        settings.put("lowStockThreshold", 5); // Batas stok rendah
        settings.put("maxRentalsPerUser", 3); // Maksimal penyewaan per user
        settings.put("uploadDirectory", uploadDirectory);
        return settings;
    }

    private Map<String, Object> getUserSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("defaultUserRole", "PELANGGAN");
        settings.put("passwordMinLength", 8);
        settings.put("allowedImageTypes", Arrays.asList("image/jpeg", "image/png"));
        settings.put("maxImageSize", 5 * 1024 * 1024); // 5MB
        return settings;
    }

    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        try {
            // Rental metrics
            metrics.put("totalRentals", penyewaanRepository.count());
            metrics.put("activeRentals", getTotalActiveRentals());
            metrics.put("overdueRentals", penyewaanRepository.findAllOverdueRentals().size());
            
            // Film metrics
            metrics.put("totalFilms", filmRepository.count());
            metrics.put("outOfStockFilms", filmRepository.countOutOfStockFilms());
            metrics.put("lowStockFilms", filmRepository.findLowStockFilms(5).size());
            
            // User metrics
            metrics.put("totalUsers", userRepository.count());
            metrics.put("activeUsers", userRepository.countActiveUsers());
            
            // Revenue metrics
            metrics.put("monthlyRevenue", calculateMonthlyRevenue());
            metrics.put("yearlyRevenue", calculateYearlyRevenue());
            
            // System health
            metrics.put("diskUsage", calculateDiskUsage());
            metrics.put("imageStorageSize", calculateImageStorageSize());
            
        } catch (Exception e) {
            logger.error("Error getting system metrics: {}", e.getMessage());
        }
        return metrics;
    }

    private Double calculateMonthlyRevenue() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        List<Penyewaan> monthlyRentals = penyewaanRepository
            .findByTanggalSewaBetween(startOfMonth, now);
            
        return monthlyRentals.stream()
            .mapToDouble(rental -> 5000.0 + (rental.isOverdue() ? 
                rental.getDurationInDays() * 1000.0 : 0.0))
            .sum();
    }

    private long calculateDiskUsage() {
        try {
            Path uploadPath = Paths.get(uploadDirectory);
            return Files.walk(uploadPath)
                .filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (Exception e) {
                        return 0L;
                    }
                })
                .sum();
        } catch (Exception e) {
            logger.error("Error calculating disk usage: {}", e.getMessage());
            return 0L;
        }
    }

    private long calculateImageStorageSize() {
        try {
            Path actorsPath = Paths.get(uploadDirectory, "actors");
            Path coversPath = Paths.get(uploadDirectory, "covers");
            
            long actorsSize = Files.walk(actorsPath)
                .filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (Exception e) {
                        return 0L;
                    }
                })
                .sum();
                
            long coversSize = Files.walk(coversPath)
                .filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (Exception e) {
                        return 0L;
                    }
                })
                .sum();
                
            return actorsSize + coversSize;
        } catch (Exception e) {
            logger.error("Error calculating image storage size: {}", e.getMessage());
            return 0L;
        }
    }

    public ResponseEntity<?> getMonthlyReport(Integer year, Integer month) {
        Map<String, Object> report = generateMonthlyReport(year, month);
        return ResponseEntity.ok(report);
    }

    public ResponseEntity<byte[]> generateMonthlyReportPDF(int year, int month) {
        try {
            Map<String, Object> reportData = generateMonthlyReport(year, month);
            // Implementasi generate PDF dari reportData
            // Contoh return kosong
            return ResponseEntity.ok().body(new byte[0]);
        } catch (Exception e) {
            logger.error("Error generating PDF report: {}", e.getMessage());
            throw new RuntimeException("Gagal generate laporan PDF", e);
        }
    }
}