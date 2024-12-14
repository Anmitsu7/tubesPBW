package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.example.demo.model.Film;

import lombok.Data;

@Data
@Entity
@Table(name = "penyewaan")
public class Penyewaan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pengguna_id")
    private User pengguna;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @Column(name = "tanggal_sewa")
    private LocalDate tanggalSewa;

    @Column(name = "tanggal_kembali")
    private LocalDate tanggalKembali;

    @Column(nullable = false)
    private String status;

    // Constructors
    public Penyewaan() {}

    public Penyewaan(User pengguna, Film film, LocalDate tanggalSewa) {
        this.pengguna = pengguna;
        this.film = film;
        this.tanggalSewa = tanggalSewa;
        this.status = "DISEWA";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getPengguna() {
        return pengguna;
    }

    public void setPengguna(User pengguna) {
        this.pengguna = pengguna;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public LocalDate getTanggalSewa() {
        return tanggalSewa;
    }

    public void setTanggalSewa(LocalDate tanggalSewa) {
        this.tanggalSewa = tanggalSewa;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(LocalDate tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Helper methods
    public void kembalikan() {
        this.status = "DIKEMBALIKAN";
        this.tanggalKembali = LocalDate.now();
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
}