package com.example.demo.controller.api.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.PenyewaanRepository;
import com.example.demo.service.AdminService;

@RestController
@RequestMapping("/api/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // Data untuk summary cards menggunakan query langsung
            statistics.put("totalRentals", jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM penyewaan", Integer.class));

            statistics.put("averageDuration", jdbcTemplate.queryForObject(
                    "SELECT AVG(rental_duration) FROM penyewaan WHERE status = 'DIKEMBALIKAN'",
                    Double.class));

            statistics.put("returnRate", jdbcTemplate.queryForObject(
                    "SELECT (COUNT(CASE WHEN status = 'DIKEMBALIKAN' THEN 1 END) * 100.0 / COUNT(*)) FROM penyewaan",
                    Double.class));

            // Monthly Rentals Chart Data
            List<Map<String, Object>> monthlyRentals = jdbcTemplate.queryForList(
                    """
                            SELECT
                                DATE_TRUNC('month', tanggal_sewa) as rental_date,
                                COUNT(*) as total_rentals
                            FROM penyewaan
                            WHERE tanggal_sewa >= CURRENT_DATE - INTERVAL '12 months'
                            GROUP BY DATE_TRUNC('month', tanggal_sewa)
                            ORDER BY rental_date
                            """);
            statistics.put("monthlyRentals", monthlyRentals);

            // Genre Distribution Chart Data
            List<Map<String, Object>> genreDistribution = jdbcTemplate.queryForList(
                    """
                            SELECT
                                g.nama as genre,
                                COUNT(p.id) as rental_count
                            FROM genre g
                            JOIN film f ON f.genre_id = g.id
                            JOIN penyewaan p ON p.film_id = f.id
                            GROUP BY g.nama
                            ORDER BY rental_count DESC
                            """);
            statistics.put("genreDistribution", genreDistribution);

            // Popular Genre
            String popularGenre = jdbcTemplate.queryForObject(
                    """
                            SELECT g.nama
                            FROM genre g
                            JOIN film f ON f.genre_id = g.id
                            JOIN penyewaan p ON p.film_id = f.id
                            GROUP BY g.nama
                            ORDER BY COUNT(*) DESC
                            LIMIT 1
                            """,
                    String.class);
            statistics.put("popularGenre", popularGenre);

            // Top Films Table
            List<Map<String, Object>> topFilms = jdbcTemplate.queryForList(
                    """
                            SELECT
                                f.judul,
                                COUNT(p.id) as totalRentals,
                                f.stok as currentStock
                            FROM film f
                            LEFT JOIN penyewaan p ON p.film_id = f.id
                            GROUP BY f.id, f.judul, f.stok
                            ORDER BY COUNT(p.id) DESC
                            LIMIT 5
                            """);
            statistics.put("topFilms", topFilms);

            // Stock Alerts Table
            List<Map<String, Object>> stockAlerts = jdbcTemplate.queryForList(
                    """
                            SELECT
                                f.judul,
                                f.stok as availableStock,
                                (f.stok + COUNT(CASE WHEN p.status = 'DISEWA' THEN 1 END)) as totalStock
                            FROM film f
                            LEFT JOIN penyewaan p ON p.film_id = f.id AND p.status = 'DISEWA'
                            GROUP BY f.id, f.judul, f.stok
                            HAVING f.stok < 3
                            ORDER BY f.stok ASC
                            """);
            statistics.put("stockAlerts", stockAlerts);

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting statistics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-films")
    public ResponseEntity<?> getTopFilms() {
        try {
            List<Map<String, Object>> topFilms = adminService.getTopFilms();
            return ResponseEntity.ok(topFilms);
        } catch (Exception e) {
            logger.error("Error getting top films: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stock-alerts")
    public ResponseEntity<?> getStockAlerts() {
        try {
            List<Map<String, Object>> alerts = adminService.getStockAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            logger.error("Error getting stock alerts: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rental-statistics")
    public ResponseEntity<?> getRentalStatistics() {
        try {
            return ResponseEntity.ok(adminService.getRentalStatistics());
        } catch (Exception e) {
            logger.error("Error fetching rental statistics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching statistics");
        }
    }

    @GetMapping("/monthly-report")
    public ResponseEntity<?> getMonthlyReport(@RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        try {
            return ResponseEntity.ok(adminService.getMonthlyReport(year, month));
        } catch (Exception e) {
            logger.error("Error generating monthly report: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating report");
        }
    }

    @GetMapping("/download-monthly-report")
    public ResponseEntity<?> downloadMonthlyReport(@RequestParam int year, @RequestParam int month) {
        try {
            return adminService.generateMonthlyReportPDF(year, month);
        } catch (Exception e) {
            logger.error("Error downloading monthly report: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading report");
        }
    }

    @GetMapping("/yearly-stats")
    public ResponseEntity<?> getYearlyStats() {
        try {
            return ResponseEntity.ok(adminService.getYearlyStats());
        } catch (Exception e) {
            logger.error("Error getting yearly statistics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching yearly statistics");
        }
    }

    @GetMapping("/system-metrics")
    public ResponseEntity<?> getSystemMetrics() {
        try {
            return ResponseEntity.ok(adminService.getSystemMetrics());
        } catch (Exception e) {
            logger.error("Error getting system metrics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching system metrics");
        }
    }

    @GetMapping("/rental-trends")
    public ResponseEntity<?> getRentalTrends() {
        try {
            return ResponseEntity.ok(adminService.getRentalTrends());
        } catch (Exception e) {
            logger.error("Error getting rental trends: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching rental trends");
        }
    }

    @GetMapping("/user-statistics")
    public ResponseEntity<?> getUserStatistics() {
        try {
            return ResponseEntity.ok(adminService.getUserStatistics());
        } catch (Exception e) {
            logger.error("Error getting user statistics: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching user statistics");
        }
    }
}