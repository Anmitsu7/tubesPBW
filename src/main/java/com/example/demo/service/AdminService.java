package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminService {
   private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

   @Autowired 
   private FilmRepository filmRepository;
   
   @Autowired 
   private PenyewaanRepository penyewaanRepository;
   
   @Autowired 
   private UserRepository userRepository;

   // Dashboard Methods
   public long getTotalActiveRentals() {
       return penyewaanRepository.countByStatus("DISEWA");
   }

   public List<Penyewaan> getRecentRentals() {
       return penyewaanRepository.findTop10ByOrderByTanggalSewaDesc();
   }

   // Rental Management Methods
   public List<Penyewaan> getActiveRentals() {
       return penyewaanRepository.findByStatus("DISEWA");
   }

   public List<Penyewaan> getReturnedRentals() {
       return penyewaanRepository.findByStatus("DIKEMBALIKAN"); 
   }

   // Statistics & Reports Methods
   public Map<String, Object> getMonthlyStats() {
       Map<String, Object> stats = new HashMap<>();
       try {
           stats.put("totalRentals", penyewaanRepository.count());
           stats.put("activeRentals", getTotalActiveRentals());
           stats.put("popularFilms", penyewaanRepository.findMostRentedFilms());
           // Add more monthly statistics as needed
       } catch (Exception e) {
           logger.error("Error getting monthly stats: {}", e.getMessage());
       }
       return stats;
   }

   public Map<String, Object> getYearlyStats() {
       Map<String, Object> stats = new HashMap<>();
       try {
           // Add yearly statistics logic
           stats.put("yearlyRentals", penyewaanRepository.countRentalsByYear());
           stats.put("yearlyRevenue", calculateYearlyRevenue());
           // Add more yearly statistics as needed
       } catch (Exception e) {
           logger.error("Error getting yearly stats: {}", e.getMessage());
       }
       return stats;
   }

   private Double calculateYearlyRevenue() {
       // Add revenue calculation logic
       return 0.0; // Placeholder
   }

   // Settings Methods
   public Map<String, Object> getAdminSettings() {
       Map<String, Object> settings = new HashMap<>();
       try {
           settings.put("systemSettings", getSystemSettings());
           settings.put("userSettings", getUserSettings());
           // Add more settings as needed
       } catch (Exception e) {
           logger.error("Error getting admin settings: {}", e.getMessage());
       }
       return settings;
   }

   private Map<String, Object> getSystemSettings() {
       // Add system settings logic
       return new HashMap<>();
   }

   private Map<String, Object> getUserSettings() {
       // Add user settings logic
       return new HashMap<>();
   }
}