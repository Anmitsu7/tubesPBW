package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

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

    // Constructors
    public Aktor() {}

    public Aktor(String nama, String negaraAsal) {
        this.nama = nama;
        this.negaraAsal = negaraAsal;
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

    // Additional helper method
    @Override
    public String toString() {
        return nama + " (" + negaraAsal + ")";
    }
}