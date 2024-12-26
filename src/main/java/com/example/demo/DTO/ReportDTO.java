package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class ReportDTO {
    // General report info
    private Integer year;
    private Integer month;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Summary statistics
    private Integer totalRentals;
    private Integer totalActiveRentals;
    private Integer totalReturnedRentals;
    private Integer totalOverdueRentals;
    private Double totalRevenue;
    private Double totalLateFees;
    
    // User metrics
    private Integer totalUsers;
    private Integer activeUsers;
    private Integer newUsers;
    
    // Film metrics
    private Integer totalFilms;
    private Integer availableFilms;
    private Integer outOfStockFilms;
    
    // Top items
    private List<TopFilmDTO> topFilms;
    private List<TopGenreDTO> topGenres;
    private List<TopUserDTO> topUsers;
    
    // Daily/Monthly breakdowns
    private List<Map<String, Object>> dailyRentals;
    private List<Map<String, Object>> monthlyRentals;
    private List<Map<String, Object>> rentalsByGenre;
    
    // Additional statistics
    private Double averageRentalDuration;
    private Double returnRate;
    private Integer repeatCustomers;

    // Nested DTOs for top items
    @Data
    public static class TopFilmDTO {
        private Long filmId;
        private String judul;
        private Integer totalRentals;
        private Integer currentStock;
        private Double rentalPercentage;
        private String genre;
    }

    @Data
    public static class TopGenreDTO {
        private Long genreId;
        private String nama;
        private Integer totalRentals;
        private Integer uniqueFilms;
        private Double rentalPercentage;
    }

    @Data
    public static class TopUserDTO {
        private Long userId;
        private String username;
        private Integer totalRentals;
        private Integer activeRentals;
        private Double averageRentalDuration;
        private LocalDate lastRentalDate;
    }

    // Constructors
    public ReportDTO() {}

    public ReportDTO(Integer year, Integer month) {
        this.year = year;
        this.month = month;
        this.startDate = LocalDate.of(year, month, 1);
        this.endDate = this.startDate.plusMonths(1).minusDays(1);
    }

    // Helper methods
    public boolean isMonthlyReport() {
        return month != null;
    }

    public String getReportPeriod() {
        if (isMonthlyReport()) {
            return String.format("%d-%02d", year, month);
        }
        return year.toString();
    }

    public Double getReturnRatePercentage() {
        if (totalRentals == 0) return 0.0;
        return (totalReturnedRentals * 100.0) / totalRentals;
    }
}