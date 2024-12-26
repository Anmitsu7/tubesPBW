package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class RentalStatisticsDTO {
    // Time-based metrics
    private Map<String, Integer> rentalsByMonth;
    private Map<String, Integer> rentalsByDay;
    private Map<LocalDate, Integer> dailyTrends;
    
    // Genre-based metrics
    private Map<String, Integer> rentalsByGenre;
    private Map<String, Double> genrePopularityPercentage;
    
    // Film metrics
    private List<FilmRentalMetric> mostRentedFilms;
    private List<FilmRentalMetric> leastRentedFilms;
    private List<FilmAvailabilityMetric> stockAlerts;
    
    // User metrics
    private List<UserRentalMetric> topCustomers;
    private List<UserActivityMetric> userActivity;
    
    // Performance metrics
    private Double averageRentalDuration;
    private Double returnRate;
    private Integer overdueRentals;
    private Double lateReturnPercentage;

    // Nested classes for detailed metrics
    @Data
    public static class FilmRentalMetric {
        private Long filmId;
        private String judul;
        private String genre;
        private Integer totalRentals;
        private Integer currentStock;
        private Double rentalFrequency;
        private Double averageDuration;
    }

    @Data
    public static class FilmAvailabilityMetric {
        private Long filmId;
        private String judul;
        private Integer totalStock;
        private Integer availableStock;
        private Integer activeRentals;
        private LocalDate lastRestock;
    }

    @Data
    public static class UserRentalMetric {
        private Long userId;
        private String username;
        private Integer totalRentals;
        private Integer currentRentals;
        private List<String> favoriteGenres;
        private Double averageRentalDuration;
        private Integer overdueCount;
    }

    @Data
    public static class UserActivityMetric {
        private String timeFrame;
        private Integer activeUsers;
        private Integer newUsers;
        private Integer returningUsers;
        private Double averageRentalsPerUser;
    }

    // Constructors
    public RentalStatisticsDTO() {}

    // Helper methods
    public void calculateGenrePercentages() {
        int totalRentals = rentalsByGenre.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
            
        genrePopularityPercentage = new java.util.HashMap<>();
        rentalsByGenre.forEach((genre, count) -> {
            double percentage = (count * 100.0) / totalRentals;
            genrePopularityPercentage.put(genre, Math.round(percentage * 100.0) / 100.0);
        });
    }

    public Map<String, Object> generateSummary() {
        Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("totalRentals", getTotalRentals());
        summary.put("averageRentalDuration", averageRentalDuration);
        summary.put("returnRate", returnRate);
        summary.put("mostPopularGenre", getMostPopularGenre());
        summary.put("topPerformingFilm", getMostRentedFilm());
        return summary;
    }

    private int getTotalRentals() {
        return rentalsByMonth.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
    }

    private String getMostPopularGenre() {
        return rentalsByGenre.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("");
    }

    private String getMostRentedFilm() {
        return mostRentedFilms.isEmpty() ? "" : 
            mostRentedFilms.get(0).getJudul();
    }
}