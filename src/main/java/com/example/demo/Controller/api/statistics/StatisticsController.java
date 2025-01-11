package com.example.demo.controller.api.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AdminService;

@RestController
@RequestMapping("/api/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

   private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

   @Autowired
   private AdminService adminService;

   @GetMapping
   public ResponseEntity<Map<String, Object>> getStatistics() {
       try {
           Map<String, Object> statistics = new HashMap<>();

           // Get system metrics
           Map<String, Object> metrics = adminService.getSystemMetrics();
           statistics.put("totalRentals", metrics.get("totalRentals"));
           statistics.put("averageDuration", adminService.calculateAverageDuration());
           statistics.put("returnRate", metrics.get("returnRate"));

           // Get monthly stats and extract data
           Map<String, Object> monthlyStats = adminService.getMonthlyStats();
           
           // Get popular genre
           List<Map<String, Object>> genreStats = (List<Map<String, Object>>) monthlyStats.get("rentalsByGenre");
           if (genreStats != null && !genreStats.isEmpty()) {
               statistics.put("popularGenre", genreStats.get(0).get("genre"));
           }

           // Add monthly rentals trend data
           Map<String, Object> trends = adminService.getRentalTrends();
           statistics.put("monthlyRentals", trends.get("dailyData"));
           
           // Add genre distribution
           statistics.put("genreDistribution", monthlyStats.get("rentalsByGenre"));

           // Add tables data
           statistics.put("topFilms", monthlyStats.get("popularFilms"));
           statistics.put("stockAlerts", adminService.getStockAlerts().get("lowStock"));

           return ResponseEntity.ok(statistics);
       } catch (Exception e) {
           logger.error("Error getting statistics: ", e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }
   }

   @GetMapping("/top-films") 
   public ResponseEntity<?> getTopFilms() {
       try {
           Map<String, Object> monthlyStats = adminService.getMonthlyStats();
           return ResponseEntity.ok(monthlyStats.get("popularFilms"));
       } catch (Exception e) {
           logger.error("Error getting top films: ", e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }
   }

   @GetMapping("/stock-alerts")
   public ResponseEntity<?> getStockAlerts() {
       try {
           Map<String, Object> stockAlerts = adminService.getStockAlerts();
           return ResponseEntity.ok(stockAlerts.get("lowStock"));
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