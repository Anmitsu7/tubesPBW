package com.rentalfilm.model;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Positive;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "penyewaan")
public class Penyewaan {
    public enum Status {
        DISEWA, DIKEMBALIKAN, TERLAMBAT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pengguna_id", nullable = false)
    private Pengguna pengguna;

    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @Column(name = "tanggal_sewa", nullable = false)
    private LocalDateTime tanggalSewa;

    @Column(name = "tanggal_kembali")
    private LocalDateTime tanggalKembali;

    @Column(name = "tanggal_jatuh_tempo")
    private LocalDateTime tanggalJatuhTempo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "total_biaya")
    @Positive(message = "Total biaya harus positif")
    private Double totalBiaya;

    @Column(name = "denda")
    private Double denda;

    public Status getStatus() {
        return this.status;
    }

    public Film getFilm() {
        return this.film;
    }

    public ChronoLocalDateTime<?> getTanggalJatuhTempo() {
        return this.tanggalJatuhTempo;
    }

    public void setDenda(double denda2) {
        this.denda = denda2;
    }

    public void setTanggalKembali(LocalDateTime now) {
        this.tanggalKembali = now;
    }

    public void setStatus(Status dikembalikan) {
        this.status = dikembalikan;
    }

    // Konstruktor, getter, dan setter
}