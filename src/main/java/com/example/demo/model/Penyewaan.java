package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "penyewaan")
public class Penyewaan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pengguna_id", nullable = false)
    private User pengguna;

    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @Column(name = "tanggal_sewa", nullable = false)
    private LocalDate tanggalSewa;

    @Column(name = "tanggal_kembali")
    private LocalDate tanggalKembali;

    @Column(nullable = false)
    private String status;

    // Additional fields
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

    // Constructors
    public Penyewaan() {}

    public Penyewaan(User pengguna, Film film, LocalDate tanggalSewa) {
        this.pengguna = pengguna;
        this.film = film;
        this.tanggalSewa = tanggalSewa;
        this.status = "DISEWA";
    }

    // JPA callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (tanggalSewa == null) {
            tanggalSewa = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business logic methods
    public void kembalikan() {
        this.status = "DIKEMBALIKAN";
        this.tanggalKembali = LocalDate.now();
        this.rentalDuration = calculateDuration();
        this.lateFee = calculateLateFee();
        if (this.film != null) {
            this.film.incrementAvailableStock();
        }
    }

    public boolean isOverdue() {
        return status.equals("DISEWA") && 
               LocalDate.now().isAfter(tanggalSewa.plusDays(7));
    }

    public long getDurationInDays() {
        if (tanggalKembali == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(tanggalSewa, tanggalKembali);
    }

    private Integer calculateDuration() {
        if (tanggalKembali == null) {
            return null;
        }
        return (int) getDurationInDays();
    }

    private Double calculateLateFee() {
        if (!isOverdue() || tanggalKembali == null) {
            return 0.0;
        }
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(
            tanggalSewa.plusDays(7), 
            tanggalKembali
        );
        return daysLate * 1000.0; // Rp 1000 per day late fee
    }

    // Validation method
    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (tanggalKembali != null && tanggalKembali.isBefore(tanggalSewa)) {
            throw new IllegalStateException("Tanggal kembali tidak boleh sebelum tanggal sewa");
        }
    }

    // Status management methods
    public boolean canBeReturned() {
        return "DISEWA".equals(status);
    }

    public void extend(int days) {
        if (!"DISEWA".equals(status)) {
            throw new IllegalStateException("Hanya penyewaan aktif yang dapat diperpanjang");
        }
        this.tanggalKembali = tanggalKembali.plusDays(days);
    }
}