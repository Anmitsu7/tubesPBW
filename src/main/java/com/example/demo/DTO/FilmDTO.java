package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FilmDTO {

    private Long id;

    private Integer totalRentals;

    @NotBlank(message = "Judul film tidak boleh kosong")
    @Size(min = 1, max = 200, message = "Judul film harus antara 1 dan 200 karakter")
    private String judul;

    @NotBlank(message = "Deskripsi film tidak boleh kosong")
    @Size(max = 1000, message = "Deskripsi tidak boleh lebih dari 1000 karakter")
    private String deskripsi;

    @NotNull(message = "Tahun rilis harus diisi")
    @Min(value = 1900, message = "Tahun rilis tidak valid")
    @Max(value = 2100, message = "Tahun rilis tidak valid")
    private Integer tahun_rilis;

    @NotNull(message = "Genre harus dipilih")
    private Long genreId;

    // Untuk menampilkan nama genre di UI
    private String genreNama;

    @NotNull(message = "Stok harus diisi")
    @Min(value = 0, message = "Stok tidak boleh negatif")
    private Integer stok;

    private String coverUrl;

    @NotEmpty(message = "Film harus memiliki minimal 1 aktor")
    private List<Long> aktorIds;

    // Untuk menampilkan nama-nama aktor di UI
    private List<String> aktorNames;

    // Metadata fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Status fields
    private Boolean isAvailable;
    private Integer availableStock;
    private Integer rentedCount;

    // Rating dan Review
    private Double averageRating;
    private Integer totalReviews;

    // Constructors
    public FilmDTO() {
    }

    public FilmDTO(Long id, String judul, String deskripsi, Integer tahun_rilis,
            Long genreId, Integer stok, String coverUrl) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tahun_rilis = tahun_rilis;
        this.genreId = genreId;
        this.stok = stok;
        this.coverUrl = coverUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Integer getTahunRilis() {
        return tahun_rilis;
    }

    public void setTahunRilis(Integer tahun_rilis) {
        this.tahun_rilis = tahun_rilis;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getGenreNama() {
        return genreNama;
    }

    public void setGenreNama(String genreNama) {
        this.genreNama = genreNama;
    }

    public Integer getStok() {
        return stok;
    }

    public void setStok(Integer stok) {
        this.stok = stok;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<Long> getAktorIds() {
        return aktorIds;
    }

    public void setAktorIds(List<Long> aktorIds) {
        this.aktorIds = aktorIds;
    }

    public List<String> getAktorNames() {
        return aktorNames;
    }

    public void setAktorNames(List<String> aktorNames) {
        this.aktorNames = aktorNames;
    }

    public Integer getTotalRentals() {
        return totalRentals;
    }

    public void setTotalRentals(Integer totalRentals) {
        this.totalRentals = totalRentals;
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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getRentedCount() {
        return rentedCount;
    }

    public void setRentedCount(Integer rentedCount) {
        this.rentedCount = rentedCount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    @Override
    public String toString() {
        return "FilmDTO{" +
                "id=" + id +
                ", judul='" + judul + '\'' +
                ", tahun_rilis=" + tahun_rilis +
                ", genreId=" + genreId +
                ", stok=" + stok +
                ", totalRentals=" + totalRentals +
                '}';
    }
}