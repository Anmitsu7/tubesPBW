package com.example.demo.dto;

import jakarta.validation.constraints.*;


import java.nio.file.Paths;
import java.util.List;


public class FilmDTO {

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
    private Integer tahunRilis;

    @NotNull(message = "Genre harus dipilih")
    private Long genreId;

    @NotNull(message = "Stok harus diisi")
    @Min(value = 0, message = "Stok tidak boleh negatif")
    private Integer stok;

    private String coverUrl;

    @NotEmpty(message = "Film harus memiliki minimal 1 aktor")
    private List<Long> aktorIds;

    // Constructors
    public FilmDTO() {}

    // Getters and Setters
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
        return tahunRilis;
    }

    public void setTahunRilis(Integer tahunRilis) {
        this.tahunRilis = tahunRilis;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
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

    public Integer getTotalRentals() {
        return totalRentals;
    }
    
    public void setTotalRentals(Integer totalRentals) {
        this.totalRentals = totalRentals;
    }
}