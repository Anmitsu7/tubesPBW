package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "film")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String judul;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    private Integer tahunRilis;

    @Column(nullable = false)
    private Integer stok;

    private String coverUrl;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToMany
    @JoinTable(
        name = "film_aktor",
        joinColumns = @JoinColumn(name = "film_id"),
        inverseJoinColumns = @JoinColumn(name = "aktor_id")
    )
    private Set<Aktor> actors;

    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // Rental tracking fields
    @Column(name = "total_rentals")
    private Integer totalRentals = 0;

    @Column(name = "available_stock")
    private Integer availableStock;

    @OneToMany(mappedBy = "film")
    private Set<Penyewaan> penyewaans;

    // Constructors
    public Film() {}

    public Film(String judul, String deskripsi, Integer tahunRilis, Integer stok) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tahunRilis = tahunRilis;
        this.stok = stok;
        this.availableStock = stok;
    }

    // JPA callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        availableStock = stok;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isAvailable() {
        return availableStock > 0;
    }

    public void incrementTotalRentals() {
        this.totalRentals++;
        this.availableStock--;
    }

    public void decrementAvailableStock() {
        if (this.availableStock > 0) {
            this.availableStock--;
        }
    }

    public void incrementAvailableStock() {
        if (this.availableStock < this.stok) {
            this.availableStock++;
        }
    }

    // Additional getters and setters if needed
}