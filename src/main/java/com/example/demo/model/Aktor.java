package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "aktor")
public class Aktor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nama;

    @Column(name = "negara_asal")
    private String negaraAsal;

    @Column(name = "foto_url")
    private String fotoUrl;

    // Relasi many-to-many dengan Film
    @ManyToMany(mappedBy = "actors", fetch = FetchType.EAGER)
    private Set<Film> films = new HashSet<>();

    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    // Metadata fields
    @Column(name = "total_films")
    private Integer totalFilms = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public Aktor() {
        this.createdAt = LocalDateTime.now();
    }

    public Aktor(String nama, String negaraAsal) {
        this();
        this.nama = nama;
        this.negaraAsal = negaraAsal;
    }

    public Aktor(String nama, String negaraAsal, String fotoUrl) {
        this(nama, negaraAsal);
        this.fotoUrl = fotoUrl;
    }

    // JPA callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void addFilm(Film film) {
        this.films.add(film);
        this.totalFilms = this.films.size();
    }

    public void removeFilm(Film film) {
        this.films.remove(film);
        this.totalFilms = this.films.size();
    }

    public boolean hasFilm(Film film) {
        return this.films.contains(film);
    }

    public boolean isInActiveFilm() {
        return this.films.stream()
                .anyMatch(film -> film.getStok() > 0);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNegaraAsal() {
        return negaraAsal;
    }

    public void setNegaraAsal(String negaraAsal) {
        this.negaraAsal = negaraAsal;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Set<Film> getFilms() {
        return films;
    }

    public void setFilms(Set<Film> films) {
        this.films = films;
        this.totalFilms = films.size();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getTotalFilms() {
        return totalFilms;
    }

    public void setTotalFilms(Integer totalFilms) {
        this.totalFilms = totalFilms;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Override methods
    @Override
    public String toString() {
        return "Aktor{" +
                "id=" + id +
                ", nama='" + nama + '\'' +
                ", negaraAsal='" + negaraAsal + '\'' +
                ", totalFilms=" + totalFilms +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Aktor))
            return false;
        Aktor aktor = (Aktor) o;
        return getId() != null && getId().equals(aktor.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}