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

    @Column(name = "tahun_rilis")
    private Integer tahunRilis;

    @Column(nullable = false)
    private Integer stok;

    @Column(name = "cover_url")
    private String coverUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "film_aktor",
        joinColumns = @JoinColumn(name = "film_id"),
        inverseJoinColumns = @JoinColumn(name = "aktor_id")
    )
    private Set<Aktor> actors;

    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
    private Set<Penyewaan> penyewaans;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Film() {
        this.stok = 0;
        this.createdAt = LocalDateTime.now();
    }

    public Film(String judul, String deskripsi, Integer tahunRilis, Integer stok) {
        this();
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tahunRilis = tahunRilis;
        this.stok = stok;
    }

    // JPA callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isAvailable() {
        return stok > 0;
    }

    public void decrementStock() {
        if (this.stok > 0) {
            this.stok--;
        } else {
            throw new IllegalStateException("Stok film sudah habis");
        }
    }

    public void incrementStock() {
        this.stok++;
    }

    // Equals and HashCode (override from @Data to prevent circular reference)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film)) return false;
        Film film = (Film) o;
        return id != null && id.equals(film.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}