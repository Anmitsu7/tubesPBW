package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class AktorDTO {
    private Long id;
    
    @NotBlank(message = "Nama aktor tidak boleh kosong")
    @Size(min = 2, max = 100, message = "Nama aktor harus antara 2 dan 100 karakter")
    private String nama;

    @NotBlank(message = "Negara asal tidak boleh kosong")
    @Size(min = 2, max = 50, message = "Negara asal harus antara 2 dan 50 karakter")
    private String negaraAsal;

    private String fotoUrl;
    
    // Tambahkan field baru
    private List<String> filmList;
    private int totalFilms;

    // Constructors
    public AktorDTO() {}
    
    public AktorDTO(Long id, String nama, String negaraAsal, String fotoUrl) {
        this.id = id;
        this.nama = nama;
        this.negaraAsal = negaraAsal;
        this.fotoUrl = fotoUrl;
    }

    // Getters and Setters yang sudah ada
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Tambahkan getter dan setter untuk field baru
    public List<String> getFilmList() {
        return filmList;
    }

    public void setFilmList(List<String> filmList) {
        this.filmList = filmList;
    }

    public int getTotalFilms() {
        return totalFilms;
    }

    public void setTotalFilms(int totalFilms) {
        this.totalFilms = totalFilms;
    }

    // Untuk memudahkan debugging
    @Override
    public String toString() {
        return "AktorDTO{" +
                "id=" + id +
                ", nama='" + nama + '\'' +
                ", negaraAsal='" + negaraAsal + '\'' +
                ", fotoUrl='" + fotoUrl + '\'' +
                ", totalFilms=" + totalFilms +
                ", filmList=" + filmList +
                '}';
    }
}