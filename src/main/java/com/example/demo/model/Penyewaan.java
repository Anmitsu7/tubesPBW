package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@Table(name = "penyewaan")
public class Penyewaan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "penyewaans"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pengguna_id")
    private User pengguna;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "penyewaans"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id")
    private Film film;

    @Column(name = "tanggal_sewa", nullable = false)
    private LocalDate tanggalSewa;

    @Column(name = "tanggal_kembali")
    private LocalDate tanggalKembali;

    @Column(nullable = false)
    private String status;

    @Column(name = "rental_duration")
    private Integer rentalDuration;

    @Column(name = "late_fee")
    private Double lateFee;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    public String getStatusDisplay() {
        return isOverdue() ? "Overdue" : "Active";
    }

    @Transient
    public double getBasicPrice() {
        return 5000.0; // Harga dasar sewa
    }

    @Transient
    public double getCurrentLateFee() {
        if (!isOverdue()) return 0.0;
        
        LocalDate batasWaktu = tanggalSewa.plusDays(7);
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(batasWaktu, LocalDate.now());
        return Math.max(0, daysLate * 1000.0);
    }

    @Transient
    public double getTotalPrice() {
        return getBasicPrice() + getCurrentLateFee();
    }

    @Transient
    public long getCurrentDaysOverdue() {
        if (!isOverdue()) return 0;
        
        LocalDate batasWaktu = tanggalSewa.plusDays(7);
        return java.time.temporal.ChronoUnit.DAYS.between(batasWaktu, LocalDate.now());
    }

    // Constructors
    public Penyewaan() {
        this.status = "DISEWA"; // Set default status
    }

    public Penyewaan(User pengguna, Film film, LocalDate tanggalSewa) {
        this();
        this.pengguna = pengguna;
        this.film = film;
        this.tanggalSewa = tanggalSewa;
    }

    // JPA callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        if (tanggalSewa == null) {
            tanggalSewa = LocalDate.now();
        }
        
        if (status == null) {
            status = "DISEWA";
        }
        
        validateDates();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        validateDates();
        
        // Update rental duration and late fee if applicable
        if (status.equals("DIKEMBALIKAN") && tanggalKembali != null) {
            this.rentalDuration = calculateDuration();
            this.lateFee = calculateLateFee();
        }
    }

    // Business logic methods
    public void kembalikan() {
        validateCanBeReturned();
        this.status = "DIKEMBALIKAN";
        this.tanggalKembali = LocalDate.now();
        this.rentalDuration = calculateDuration();
        this.lateFee = calculateLateFee();
        if (this.film != null) {
            this.film.incrementStock();
        }
    }

    private void validateCanBeReturned() {
        if (!canBeReturned()) {
            throw new IllegalStateException("Penyewaan tidak dapat dikembalikan karena status: " + status);
        }
    }

    public boolean isOverdue() {
        if (!status.equals("DISEWA")) {
            return false;
        }
        LocalDate batasWaktu = tanggalSewa.plusDays(7);
        return LocalDate.now().isAfter(batasWaktu);
    }

    public long getDurationInDays() {
        if (tanggalKembali == null || tanggalSewa == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(tanggalSewa, tanggalKembali);
    }

    private Integer calculateDuration() {
        if (tanggalKembali == null || tanggalSewa == null) {
            return null;
        }
        return (int) getDurationInDays();
    }

    private Double calculateLateFee() {
        if (!isOverdue() || tanggalKembali == null) {
            return 0.0;
        }
        LocalDate batasWaktu = tanggalSewa.plusDays(7);
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(batasWaktu, tanggalKembali);
        return Math.max(0, daysLate * 1000.0); // Minimum 0 Rupiah
    }

    // Validation methods
    private void validateDates() {
        if (tanggalKembali != null && tanggalKembali.isBefore(tanggalSewa)) {
            throw new IllegalStateException("Tanggal kembali tidak boleh sebelum tanggal sewa");
        }
    }

    public boolean canBeReturned() {
        return "DISEWA".equals(status);
    }

    public void extend(int days) {
        if (!"DISEWA".equals(status)) {
            throw new IllegalStateException("Hanya penyewaan aktif yang dapat diperpanjang");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Jumlah hari perpanjangan harus positif");
        }
        LocalDate newTanggalKembali = (tanggalKembali != null) ? 
            tanggalKembali.plusDays(days) : 
            tanggalSewa.plusDays(days);
        this.tanggalKembali = newTanggalKembali;
    }

    // Getter and setter untuk ID (karena menggunakan @Data dari Lombok)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}