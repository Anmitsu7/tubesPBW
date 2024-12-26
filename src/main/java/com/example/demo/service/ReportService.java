package com.example.demo.service;

import com.example.demo.dto.ReportDTO;
import com.example.demo.dto.RentalStatisticsDTO;
import com.example.demo.repository.PenyewaanRepository;
import com.example.demo.repository.FilmRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.Film;
import com.example.demo.model.Penyewaan;
import com.example.demo.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private PenyewaanRepository penyewaanRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UserRepository userRepository;

    // Monthly Report Generation
    public ReportDTO getMonthlyReport(int year, int month) {
        try {
            ReportDTO report = new ReportDTO(year, month);
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

            // Set basic metrics
            report.setTotalRentals(countRentalsInPeriod(startDate, endDate));
            report.setTotalActiveRentals(countActiveRentals());
            report.setTotalReturnedRentals(countReturnedRentals(startDate, endDate));
            report.setTotalOverdueRentals(countOverdueRentals());

            // Calculate revenue and fees
            Map<String, Double> financials = calculateFinancials(startDate, endDate);
            report.setTotalRevenue(financials.get("revenue"));
            report.setTotalLateFees(financials.get("lateFees"));

            // Set user metrics
            report.setTotalUsers((int) userRepository.count());
            report.setActiveUsers(countActiveUsers(startDate, endDate));
            report.setNewUsers(countNewUsers(startDate, endDate));

            // Set film metrics
            report.setTotalFilms((int) filmRepository.count());
            report.setAvailableFilms((int) filmRepository.countByStokGreaterThan(0));
            report.setOutOfStockFilms(filmRepository.countOutOfStockFilms().intValue());

            // Set top performers
            report.setTopFilms(getTopFilms(startDate, endDate));
            report.setTopGenres(getTopGenres(startDate, endDate));
            report.setTopUsers(getTopUsers(startDate, endDate));

            // Set breakdowns
            report.setDailyRentals(penyewaanRepository.getDailyRentals(startDate, endDate));
            report.setMonthlyRentals(penyewaanRepository.getMonthlyRentals(year));
            report.setRentalsByGenre(penyewaanRepository.getGenrePopularity(year));

            // Calculate averages
            report.setAverageRentalDuration(calculateAverageRentalDuration(startDate, endDate));
            report.setReturnRate(calculateReturnRate(startDate, endDate));
            report.setRepeatCustomers(countRepeatCustomers(startDate, endDate));

            return report;
        } catch (Exception e) {
            logger.error("Error generating monthly report for {}-{}: {}", year, month, e.getMessage());
            throw new RuntimeException("Failed to generate monthly report", e);
        }
    }

    // Rental Statistics
    public RentalStatisticsDTO getRentalStatistics() {
        try {
            RentalStatisticsDTO statistics = new RentalStatisticsDTO();
            LocalDate today = LocalDate.now();
            LocalDate startOfMonth = today.withDayOfMonth(1);
            LocalDate startOfYear = today.withDayOfYear(1);

            // Set time-based metrics
            statistics.setRentalsByMonth(getRentalsByMonth(startOfYear, today));
            statistics.setRentalsByDay(getRentalsByDay(startOfMonth, today));
            statistics.setDailyTrends(getDailyTrends(today.minusDays(30), today));

            // Set genre metrics
            Map<String, Integer> genreStats = getGenreStatistics();
            statistics.setRentalsByGenre(genreStats);
            statistics.calculateGenrePercentages();

            // Set film metrics
            statistics.setMostRentedFilms(getMostRentedFilmsMetrics(10));
            statistics.setLeastRentedFilms(getLeastRentedFilmsMetrics(10));
            statistics.setStockAlerts(getStockAlerts());

            // Set user metrics
            statistics.setTopCustomers(getTopCustomersMetrics(10));
            statistics.setUserActivity(getUserActivityMetrics());

            // Set performance metrics
            statistics.setAverageRentalDuration(calculateAverageRentalDuration(startOfMonth, today));
            statistics.setReturnRate(calculateReturnRate(startOfMonth, today));
            statistics.setOverdueRentals(countOverdueRentals());
            statistics.setLateReturnPercentage(calculateLateReturnPercentage(startOfMonth, today));

            return statistics;
        } catch (Exception e) {
            logger.error("Error generating rental statistics: {}", e.getMessage());
            throw new RuntimeException("Failed to generate rental statistics", e);
        }
    }

    // PDF Report Generation
    public ResponseEntity<byte[]> generateMonthlyReportPDF(int year, int month) {
        try {
            ReportDTO reportData = getMonthlyReport(year, month);
            // Implement PDF generation logic here
            byte[] pdfContent = generatePDFContent(reportData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", 
                String.format("report-%d-%02d.pdf", year, month));

            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
        } catch (Exception e) {
            logger.error("Error generating PDF report for {}-{}: {}", year, month, e.getMessage());
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    // Helper methods
    private int countRentalsInPeriod(LocalDate startDate, LocalDate endDate) {
        return penyewaanRepository.findByTanggalSewaBetween(startDate, endDate).size();
    }

    private int countActiveRentals() {
        return penyewaanRepository.findByStatus("DISEWA").size();
    }

    private int countReturnedRentals(LocalDate startDate, LocalDate endDate) {
        return (int) penyewaanRepository.findByTanggalSewaBetween(startDate, endDate)
            .stream()
            .filter(p -> "DIKEMBALIKAN".equals(p.getStatus()))
            .count();
    }

    private int countOverdueRentals() {
        return penyewaanRepository.findAllOverdueRentals().size();
    }

    private Map<String, Double> calculateFinancials(LocalDate startDate, LocalDate endDate) {
        List<Penyewaan> rentals = penyewaanRepository.findByTanggalSewaBetween(startDate, endDate);
        double revenue = 0.0;
        double lateFees = 0.0;

        for (Penyewaan rental : rentals) {
            revenue += 5000.0; // Base rental fee
            if (rental.isOverdue()) {
                long daysLate = rental.getDurationInDays() - 7;
                lateFees += daysLate * 1000.0; // Late fee per day
            }
        }

        Map<String, Double> result = new HashMap<>();
        result.put("revenue", revenue);
        result.put("lateFees", lateFees);
        return result;
    }

    private int countActiveUsers(LocalDate startDate, LocalDate endDate) {
        return (int) penyewaanRepository.findByTanggalSewaBetween(startDate, endDate)
            .stream()
            .map(p -> p.getPengguna().getId())
            .distinct()
            .count();
    }

    private int countNewUsers(LocalDate startDate, LocalDate endDate) {
        // Implementation dependent on User creation date field
        return 0; // Placeholder
    }

    private List<ReportDTO.TopFilmDTO> getTopFilms(LocalDate startDate, LocalDate endDate) {
        // Implementation for top films
        return new ArrayList<>(); // Placeholder
    }

    private List<ReportDTO.TopGenreDTO> getTopGenres(LocalDate startDate, LocalDate endDate) {
        // Implementation for top genres
        return new ArrayList<>(); // Placeholder
    }

    private List<ReportDTO.TopUserDTO> getTopUsers(LocalDate startDate, LocalDate endDate) {
        // Implementation for top users
        return new ArrayList<>(); // Placeholder
    }

    private double calculateAverageRentalDuration(LocalDate startDate, LocalDate endDate) {
        List<Penyewaan> completedRentals = penyewaanRepository.findByTanggalSewaBetween(startDate, endDate)
            .stream()
            .filter(p -> "DIKEMBALIKAN".equals(p.getStatus()))
            .collect(Collectors.toList());

        if (completedRentals.isEmpty()) {
            return 0.0;
        }

        double totalDuration = completedRentals.stream()
            .mapToLong(Penyewaan::getDurationInDays)
            .sum();

        return totalDuration / completedRentals.size();
    }

    private double calculateReturnRate(LocalDate startDate, LocalDate endDate) {
        List<Penyewaan> rentals = penyewaanRepository.findByTanggalSewaBetween(startDate, endDate);
        if (rentals.isEmpty()) {
            return 0.0;
        }

        long returnedCount = rentals.stream()
            .filter(p -> "DIKEMBALIKAN".equals(p.getStatus()))
            .count();

        return (double) returnedCount / rentals.size() * 100;
    }

    private int countRepeatCustomers(LocalDate startDate, LocalDate endDate) {
        // Implementation for counting repeat customers
        return 0; // Placeholder
    }

    private byte[] generatePDFContent(ReportDTO reportData) {
        // Implement PDF generation logic
        return new byte[0]; // Placeholder
    }

    // Additional helper methods for RentalStatisticsDTO
    private Map<String, Integer> getRentalsByMonth(LocalDate startDate, LocalDate endDate) {
        // Implementation
        return new HashMap<>(); // Placeholder
    }

    private Map<String, Integer> getRentalsByDay(LocalDate startDate, LocalDate endDate) {
        // Implementation
        return new HashMap<>(); // Placeholder
    }

    private Map<LocalDate, Integer> getDailyTrends(LocalDate startDate, LocalDate endDate) {
        // Implementation
        return new HashMap<>(); // Placeholder
    }

    private Map<String, Integer> getGenreStatistics() {
        // Implementation
        return new HashMap<>(); // Placeholder
    }

    private List<RentalStatisticsDTO.FilmRentalMetric> getMostRentedFilmsMetrics(int limit) {
        // Implementation
        return new ArrayList<>(); // Placeholder
    }

    private List<RentalStatisticsDTO.FilmRentalMetric> getLeastRentedFilmsMetrics(int limit) {
        // Implementation
        return new ArrayList<>(); // Placeholder
    }

    private List<RentalStatisticsDTO.FilmAvailabilityMetric> getStockAlerts() {
        // Implementation
        return new ArrayList<>(); // Placeholder
    }

    private List<RentalStatisticsDTO.UserRentalMetric> getTopCustomersMetrics(int limit) {
        // Implementation
        return new ArrayList<>(); // Placeholder
    }

    private List<RentalStatisticsDTO.UserActivityMetric> getUserActivityMetrics() {
        // Implementation
        return new ArrayList<>(); // Placeholder
    }

    private double calculateLateReturnPercentage(LocalDate startDate, LocalDate endDate) {
        // Implementation
        return 0.0; // Placeholder
    }
}