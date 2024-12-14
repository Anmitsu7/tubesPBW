package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AktorDTO {
    
    @NotBlank(message = "Nama aktor tidak boleh kosong")
    @Size(min = 2, max = 100, message = "Nama aktor harus antara 2 dan 100 karakter")
    private String nama;

    @NotBlank(message = "Negara asal tidak boleh kosong")
    @Size(min = 2, max = 50, message = "Negara asal harus antara 2 dan 50 karakter")
    private String negaraAsal;

    private String fotoUrl;

    // Constructors
    public AktorDTO() {}

    // Getters and Setters
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

    // Untuk memudahkan debugging
    @Override
    public String toString() {
        return "AktorDTO{" +
                "nama='" + nama + '\'' +
                ", negaraAsal='" + negaraAsal + '\'' +
                ", fotoUrl='" + fotoUrl + '\'' +
                '}';
    }
}